package com.example.monitoreoasma.presentation.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreoasma.presentation.ui.components.LineChart
import com.example.monitoreoasma.presentation.ui.components.BarChart

data class RegistroAsma(
    val fecha: String,
    val sintomas: String,
    val calidadAire: String,
    val riesgo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onOpenDrawer: () -> Unit
) {
    val historialSimulado = listOf(
        RegistroAsma("2025-06-01", "Tos, Dificultad", "Mala", "Alto"),
        RegistroAsma("2025-06-03", "Dificultad", "Regular", "Alto"),
        RegistroAsma("2025-06-08", "Tos", "Mala", "Alto")
    )

    val datosLinea = listOf(3f, 2f, 4f, 1f, 3f)
    val etiquetas = listOf("01/06", "03/06", "05/06", "08/06", "10/06")
    val frecuenciaSintomas = mapOf("Tos" to 5f, "Dificultad" to 4f, "Inhalador" to 2f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información Histórica") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Text("Historial de Riesgos Altos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(historialSimulado) { registro ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Fecha: ${registro.fecha}")
                        Text("Síntomas: ${registro.sintomas}")
                        Text("Calidad del aire: ${registro.calidadAire}")
                        Text("Riesgo: ${registro.riesgo}", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Evolución de Crisis Asmáticas", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LineChart(datos = datosLinea, etiquetas = etiquetas)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Síntomas más frecuentes", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                BarChart(datos = frecuenciaSintomas)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
