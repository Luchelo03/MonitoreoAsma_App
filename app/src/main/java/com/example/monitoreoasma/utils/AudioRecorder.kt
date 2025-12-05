package com.example.monitoreoasma.utils

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.coroutineContext
import kotlin.math.abs
import kotlin.math.max

class WavAudioRecorder(
    private val context: Context,
    private val outputFile: File,
    private val sampleRate: Int = 16000,
    private val onAmplitude: ((Int) -> Unit)? = null   // 🔥 nuevo callback
) {

    private var audioRecord: AudioRecord? = null
    private var bufferSize = 0
    private var isRecording = false

    private fun hasRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun startRecording() = withContext(Dispatchers.IO) {

        if (!hasRecordPermission()) {
            throw SecurityException("RECORD_AUDIO permission not granted")
        }

        bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = try {
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            throw SecurityException("No se pudo inicializar AudioRecord. Permiso no garantizado.")
        }

        val pcmFile = File(outputFile.parent, "temp_recording.pcm")
        if (pcmFile.exists()) pcmFile.delete()

        val fos = FileOutputStream(pcmFile)

        isRecording = true
        audioRecord?.startRecording()

        val buffer = ByteArray(bufferSize)

        while (coroutineContext.isActive && isRecording) {
            val read = audioRecord!!.read(buffer, 0, buffer.size)
            if (read > 0) {
                fos.write(buffer, 0, read)

                // 🔥 calcular amplitud aproximada del chunk
                onAmplitude?.let { cb ->
                    val amp = calculateAmplitude(buffer, read)
                    // actualizamos en el hilo principal para no romper Compose
                    withContext(Dispatchers.Main) {
                        cb(amp)
                    }
                }
            }
        }

        fos.flush()
        fos.close()

        convertPcmToWav(pcmFile, outputFile, sampleRate)
        pcmFile.delete()
    }

    fun stopRecording() {
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
        audioRecord = null
    }

    // -------- cálculo de amplitud máxima de este buffer --------
    private fun calculateAmplitude(buffer: ByteArray, readBytes: Int): Int {
        var maxAmp = 0
        var i = 0
        while (i + 1 < readBytes) {
            // 16-bit little endian
            val low = buffer[i].toInt()
            val high = buffer[i + 1].toInt()
            val sample = (high shl 8) or (low and 0xFF)
            maxAmp = max(maxAmp, abs(sample))
            i += 2
        }
        return maxAmp // rango aprox: 0..32767
    }

    // -------- conversión PCM → WAV --------
    private fun convertPcmToWav(pcmFile: File, wavFile: File, sampleRate: Int) {
        val pcmData = pcmFile.readBytes()
        val wavStream = FileOutputStream(wavFile)

        val totalDataLen = pcmData.size + 36
        val byteRate = sampleRate * 2

        val header = byteArrayOf(
            'R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 'F'.code.toByte(),
            (totalDataLen and 0xff).toByte(),
            ((totalDataLen shr 8) and 0xff).toByte(),
            ((totalDataLen shr 16) and 0xff).toByte(),
            ((totalDataLen shr 24) and 0xff).toByte(),
            'W'.code.toByte(), 'A'.code.toByte(), 'V'.code.toByte(), 'E'.code.toByte(),
            'f'.code.toByte(), 'm'.code.toByte(), 't'.code.toByte(), ' '.code.toByte(),
            16, 0, 0, 0,
            1, 0,
            1, 0,
            (sampleRate and 0xff).toByte(),
            ((sampleRate shr 8) and 0xff).toByte(),
            ((sampleRate shr 16) and 0xff).toByte(),
            ((sampleRate shr 24) and 0xff).toByte(),
            (byteRate and 0xff).toByte(),
            ((byteRate shr 8) and 0xff).toByte(),
            ((byteRate shr 16) and 0xff).toByte(),
            ((byteRate shr 24) and 0xff).toByte(),
            2, 0,
            16, 0,
            'd'.code.toByte(), 'a'.code.toByte(), 't'.code.toByte(), 'a'.code.toByte(),
            (pcmData.size and 0xff).toByte(),
            ((pcmData.size shr 8) and 0xff).toByte(),
            ((pcmData.size shr 16) and 0xff).toByte(),
            ((pcmData.size shr 24) and 0xff).toByte()
        )

        wavStream.write(header)
        wavStream.write(pcmData)
        wavStream.flush()
        wavStream.close()
    }
}
