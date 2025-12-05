package com.example.monitoreoasma.presentation.ui.test

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreoasma.R
import com.example.monitoreoasma.utils.WavAudioRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecordingScreen(
    onRecordingFinished: (String) -> Unit,
    onOpenDrawer: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(10) }

    val context = LocalContext.current

    // Permiso de audio
    var hasAudioPermission by remember { mutableStateOf(false) }
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasAudioPermission = granted
            if (!granted) {
                Toast.makeText(context, "Se requiere permiso de micrófono", Toast.LENGTH_LONG).show()
            }
        }
    )

    val outputWavFile = remember { File(context.cacheDir, "grabacion_temporal.wav") }
    var recorder by remember { mutableStateOf<WavAudioRecorder?>(null) }
    var amplitude by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grabar Respiración") },
                navigationIcon = {
                    IconButton(onClick = { onOpenDrawer() }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "$secondsLeft",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de nivel de audio
                val normalized = (amplitude / 32767f).coerceIn(0f, 1f)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nivel de sonido",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(Color.LightGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(normalized)
                                .background(Color.Green)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                IconButton(
                    onClick = {
                        if (!hasAudioPermission) {
                            audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        } else {
                            if (!isRecording) {
                                isRecording = true
                                secondsLeft = 10

                                recorder = WavAudioRecorder(
                                    context = context,
                                    outputFile = outputWavFile,
                                    onAmplitude = { amp ->
                                        amplitude = amp
                                    }
                                )

                                scope.launch {
                                    println("DEBUG → WAV OUTPUT PATH = ${outputWavFile.absolutePath}")

                                    // Iniciar grabación en background
                                    val job = launch(Dispatchers.IO) {
                                        recorder?.startRecording()
                                    }

                                    // Cuenta regresiva
                                    while (secondsLeft > 0) {
                                        delay(1000)
                                        secondsLeft--
                                    }

                                    // Detener grabación
                                    recorder?.stopRecording()
                                    job.join()

                                    isRecording = false

                                    println("DEBUG → NAV to verificacion with: ${outputWavFile.absolutePath}")
                                    onRecordingFinished(outputWavFile.absolutePath)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(if (isRecording) Color.Red else MaterialTheme.colorScheme.primary),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mic),
                        contentDescription = "Micrófono",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
