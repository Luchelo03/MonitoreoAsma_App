package com.example.monitoreoasma.presentation.ui.recomendations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Recomendacion(
    val titulo: String,
    val descripcion: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecomendacionesMedicasScreen(
    drawerState: DrawerState,
    onOpenDrawer: () -> Unit
) {
    val recomendaciones = listOf(
        Recomendacion("¿Qué hacer ante una crisis?", "Mantén la calma, usa el inhalador de rescate y acude a emergencias si no mejora."),
        Recomendacion("Prevención en invierno", "Evita exposición al frío extremo. Usa ropa abrigadora y ventiladores con filtro."),
        Recomendacion("Monitoreo diario", "Registra síntomas, temperatura y uso del inhalador para mayor control."),
        Recomendacion("Alerta escolar", "Informa a los profesores y entrega un plan de acción ante crisis."),
        Recomendacion("Calidad del aire", "Evita actividades al aire libre si el aire está contaminado.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomendaciones médicas") },
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
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(recomendaciones) { reco ->
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .height(200.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(reco.titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(reco.descripcion, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Fuente: OMS, AEP, MINSA", fontWeight = FontWeight.Medium)
        }
    }
}
