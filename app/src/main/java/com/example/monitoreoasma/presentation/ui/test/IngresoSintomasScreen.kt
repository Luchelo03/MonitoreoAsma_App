package com.example.monitoreoasma.presentation.ui.test

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoSintomasScreen(
    drawerState: DrawerState,
    onOpenDrawer: () -> Unit,
    onGuardarClick: (tos: Boolean, disnea: Boolean, usoInhalador: Boolean) -> Unit
) {
    var tieneTos by remember { mutableStateOf(false) }
    var tieneDisnea by remember { mutableStateOf(false) }
    var usoInhalador by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingreso de Síntomas") },
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
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("¿Tiene tos?", fontSize = 18.sp)
            Switch(
                checked = tieneTos,
                onCheckedChange = { tieneTos = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("¿Tiene dificultad para respirar?", fontSize = 18.sp)
            Switch(
                checked = tieneDisnea,
                onCheckedChange = { tieneDisnea = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("¿Usó su inhalador?", fontSize = 18.sp)
            Switch(
                checked = usoInhalador,
                onCheckedChange = { usoInhalador = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                onGuardarClick(tieneTos, tieneDisnea, usoInhalador)
            }) {
                Text("Guardar")
            }
        }
    }
}
