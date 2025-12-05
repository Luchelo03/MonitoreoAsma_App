package com.example.monitoreoasma.presentation.ui.test

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(
    riesgo: String,
    score: Double,
    label: String,
    positiveWindows: Int,
    evidenceSeconds: Double,
    aqi: Int,
    onOpenDrawer: () -> Unit,
    onVerHistorialClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados del análisis") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Resumen del test",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Etiqueta de grabación: $label", fontSize = 18.sp)
            Text("Ventanas positivas: $positiveWindows", fontSize = 18.sp)
            Text("Segundos de evidencia: $evidenceSeconds", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Nivel de riesgo: $riesgo", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text("Score estimado: ${"%.3f".format(score)}", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Calidad de aire (AQI, si disponible): $aqi", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onVerHistorialClick) {
                Text("Ver historial de tests")
            }
        }
    }
}
