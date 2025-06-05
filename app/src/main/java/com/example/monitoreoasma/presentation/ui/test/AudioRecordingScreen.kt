package com.example.monitoreoasma.presentation.ui.test

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecordingScreen(
    onRecordingFinished: (String) -> Unit, // ahora recibe el path
    onOpenDrawer: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(10) }

    val context = LocalContext.current
    val audioFile = File(context.cacheDir, "grabacion_temporal.wav")
    val audioFilePath = audioFile.absolutePath

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

                Spacer(modifier = Modifier.height(32.dp))

                IconButton(
                    onClick = {
                        if (!isRecording) {
                            isRecording = true
                            scope.launch {


                                while (secondsLeft > 0) {
                                    delay(1000)
                                    secondsLeft--
                                }

                                onRecordingFinished(audioFilePath)
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
