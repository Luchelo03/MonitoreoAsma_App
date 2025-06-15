package com.example.monitoreoasma.presentation.ui.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(
    riesgo: String = "Alto", // Por ahora simulado
    sintomas: List<String> = listOf("Tos", "Dificultad para respirar"),
    usoInhalador: Boolean = true,
    calidadAire: String = "Mala",
    onOpenDrawer: () -> Unit,
    onVerHistorialClick: () -> Unit
) {
    val (colorRiesgo, mensaje) = when (riesgo) {
        "Bajo" -> Color.Green to "Todo en orden, puede seguir con sus actividades."
        "Medio" -> Color.Yellow to "Riesgo moderado. Evite esfuerzos físicos."
        else -> Color.Red to "Riesgo alto. Se recomienda no salir y tomar precauciones."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados del Test") },
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(colorRiesgo, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = riesgo,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = mensaje,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Análisis del resultado", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Síntomas: ${sintomas.joinToString(", ")}")
                        Text("• Calidad del aire: $calidadAire")
                        Text("• Usó inhalador: ${if (usoInhalador) "Sí" else "No"}")
                    }
                }
            }

            Button(onClick = onVerHistorialClick) {
                Text("Ver historial")
            }
        }
    }
}
