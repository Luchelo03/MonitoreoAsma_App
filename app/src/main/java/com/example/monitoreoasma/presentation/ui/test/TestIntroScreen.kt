package com.example.monitoreoasma.presentation.ui.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreoasma.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestIntroScreen(
    drawerState: DrawerState,
    onOpenDrawer: () -> Unit,
    onGrabarClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(1) }

    val instructions = listOf(
        InstructionData(
            text = "Situate en un ambiente libre de ruidos externos (esto ayudará al resultado final a tener una mejor precisión).",
            imageRes = R.drawable.recomendacion1 // tu imagen 1
        ),
        InstructionData(
            text = "Coloca el celular cerca del pecho o espalda del niño (a unos 5–10 cm)\n\nPide al niño que esté sentado o de pie, tranquilo. La grabación será más clara si colocas el micrófono a la altura del tórax o espalda alta, sin tocar directamente la ropa o la piel.",
            imageRes = R.drawable.recomendacion2
        ),
        InstructionData(
            text = "Presiona “Grabar” y deja que el niño respire normalmente durante 6–10 segundos\n\nSi el niño tose de manera natural durante ese tiempo, no detengas la grabación. Evita que hable o se mueva. Al finalizar, presiona “Detener” para enviar el audio al sistema.",
            imageRes = R.drawable.recomendacion3
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Realizar Test") },
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
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = instructions[currentStep - 1].text,
                        fontSize = 18.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = instructions[currentStep - 1].imageRes),
                        contentDescription = "Imagen del paso",
                        modifier = Modifier
                            .height(180.dp)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Número de paso y acción
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentStep}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (currentStep < 3) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Siguiente",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(12.dp)
                                    .clickable {
                                        if (currentStep < 3) currentStep++
                                    },
                                tint = Color.White
                            )
                        } else {
                            Button(onClick = onGrabarClick) {
                                Text("Grabar respiración")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class InstructionData(val text: String, val imageRes: Int)

@Preview(showSystemUi = true)
@Composable
fun TestIntroPreview() {
    TestIntroScreen(
        drawerState = rememberDrawerState(DrawerValue.Closed),
        onOpenDrawer = {},
        onGrabarClick = {}
    )
}
