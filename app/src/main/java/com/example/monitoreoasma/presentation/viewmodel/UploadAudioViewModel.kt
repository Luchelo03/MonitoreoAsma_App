package com.example.monitoreoasma.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreoasma.data.local.preferences.SessionStore
import com.example.monitoreoasma.data.remote.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class UploadAudioViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val session = SessionStore(application)

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _result = MutableStateFlow<JSONObject?>(null)
    val result = _result.asStateFlow()

    fun upload(
        childId: String,
        audioFile: File,
        checklistJson: String
    ) {
        Log.d("UploadAudio", "upload() llamado con childId=$childId, file=${audioFile.absolutePath}")
        _loading.value = true
        _error.value = null
        _result.value = null

        viewModelScope.launch {
            try {
                val token = session.tokenFlow.first() ?: run {
                    _error.value = "Token inválido."
                    _loading.value = false
                    return@launch
                }

                Log.d("UploadAudio", "Llamando a ApiClient.uploadAudioRecording...")

                val response = ApiClient.uploadAudioRecording(
                    token = token,
                    childId = childId,
                    audioFile = audioFile,
                    checklistJson = checklistJson
                )

                if (response == null) {
                    _error.value = "No se pudo subir el audio."
                } else {
                    Log.d("UploadAudio", "Respuesta de uploadAudioRecording: ${response.toString(100)}")
                    _result.value = response
                }

            } catch (e: Exception) {
                Log.e("UploadAudio", "Error subiendo audio", e)
                _error.value = e.message ?: "Error desconocido."
            } finally {
                _loading.value = false
                Log.d(
                    "UploadAudio",
                    "upload() terminó. loading=${_loading.value}, error=${_error.value}, result=${_result.value != null}"
                )
            }
        }
    }
}
