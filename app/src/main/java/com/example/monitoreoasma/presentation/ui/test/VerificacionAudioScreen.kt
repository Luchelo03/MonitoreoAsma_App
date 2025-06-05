package com.example.monitoreoasma.presentation.ui.test

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificacionAudioScreen(
    audioFilePath: String,
    drawerState: DrawerState,
    onOpenDrawer: () -> Unit,
    onRepetirClick: () -> Unit,
    onEnviarClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verificación de audio") },
                navigationIcon = {
                    IconButton(onClick = { onOpenDrawer() }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!isPlaying) {
                        val file = File(audioFilePath)
                        if (file.exists()) {
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(file.absolutePath)
                                prepare()
                                start()
                                isPlaying = true
                                setOnCompletionListener {
                                    isPlaying = false
                                    release()
                                }
                            }
                        }
                    } else {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                        isPlaying = false
                    }
                }
            ) {
                Text(if (isPlaying) "Detener reproducción" else "Reproducir audio")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Si no se escucha bien el audio, por favor vuelve a grabarlo.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    File(audioFilePath).delete() // elimina el audio anterior
                    onRepetirClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Repetir grabación")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    mediaPlayer?.release()
                    onEnviarClick()
                }
            ) {
                Text("Enviar para análisis")
            }
        }
    }
}
