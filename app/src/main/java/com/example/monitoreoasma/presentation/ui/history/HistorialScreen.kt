package com.example.monitoreoasma.presentation.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitoreoasma.presentation.viewmodel.HistorialViewModel

// -------------------- Util: conversión de fecha ISO (API 24+) --------------------
private fun isoUtcToLocal(iso: String): String {
    val patterns = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mmXXX",
        "yyyy-MM-dd" // fallback
    )
    for (p in patterns) {
        try {
            val inFmt = java.text.SimpleDateFormat(p, java.util.Locale.US)
            inFmt.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = inFmt.parse(iso)
            if (date != null) {
                val outFmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                outFmt.timeZone = java.util.TimeZone.getDefault()
                return outFmt.format(date)
            }
        } catch (_: Exception) { /* probar siguiente patrón */ }
    }
    return iso // si no pudo parsear, devuelve tal cual
}

// -------------------- Modelo UI para pintar cada ítem --------------------
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
    // ViewModel y estados
    val vm: HistorialViewModel = viewModel()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val testRuns by vm.items.collectAsState()
    val hasMore by vm.hasMore.collectAsState()
    val loadingMore by vm.loadingMore.collectAsState()

    // Carga inicial
    LaunchedEffect(Unit) { vm.loadInitial() }

    // Construcción del listado para UI
    val historial = remember(testRuns) {
        testRuns.map { it ->
            RegistroAsma(
                fecha = isoUtcToLocal(it.started_at),
                sintomas = "—",          // se llenará cuando conectemos detalle
                calidadAire = "—",       // se llenará cuando conectemos detalle
                riesgo = when (it.risk.level.lowercase()) {
                    "alto" -> "Alto"
                    "medio" -> "Medio"
                    else -> "Bajo"
                }
            )
        }
    }

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
            when {
                loading -> {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                error != null -> {
                    item {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                error ?: "Error al cargar",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { vm.loadInitial() }) { Text("Reintentar") }
                        }
                    }
                }

                historial.isEmpty() -> {
                    item {
                        Text(
                            "Aún no hay pruebas registradas.",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                else -> {
                    item {
                        Text(
                            "Historial de Riesgos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(historial) { registro ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Fecha: ${registro.fecha}")
                                Text("Síntomas: ${registro.sintomas}")
                                Text("Calidad del aire: ${registro.calidadAire}")
                                Text(
                                    "Riesgo: ${registro.riesgo}",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Pie de lista: paginación
                    item {
                        Spacer(Modifier.height(8.dp))
                        if (hasMore) {
                            if (loadingMore) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                Button(
                                    onClick = { vm.loadMore() },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Cargar más") }
                            }
                        } else {
                            Text(
                                "No hay más resultados.",
                                modifier = Modifier.padding(vertical = 12.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}
