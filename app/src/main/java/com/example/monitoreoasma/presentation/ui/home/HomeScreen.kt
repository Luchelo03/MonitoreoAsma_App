package com.example.monitoreoasma.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.monitoreoasma.presentation.ui.home.components.AsthmaAttackChart
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.monitoreoasma.presentation.ui.home.components.AirQualityCard
import com.example.monitoreoasma.domain.model.AirQualityData
import com.example.monitoreoasma.domain.usecase.obtenerCalidadAireUseCase
import kotlinx.coroutines.Dispatchers


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateTo: (String) -> Unit = {},
    userName: String = "usuario"
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var data by remember { mutableStateOf<AirQualityData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawerContent(userName = userName, onOptionSelected = onNavigateTo)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "¡Hola $userName!", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            // Aquí puedes manejar notificaciones más adelante
                        }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones"
                            )
                        }
                    }
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {


                    LaunchedEffect(Unit) {
                        scope.launch(Dispatchers.IO) {
                            val result = obtenerCalidadAireUseCase()
                            data = result
                            isLoading = false
                        }
                    }

                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        data?.let {
                            AirQualityCard(airData = it)
                        } ?: run {
                            Text("Error al cargar los datos", modifier = Modifier.padding(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gráfico de ataques asmáticos
                    AsthmaAttackChart()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mensaje informativo final
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "No olvides activar las notificaciones para mayor información del asma en niños.",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        userName = "usuario",
        onNavigateTo = {}
    )
}
