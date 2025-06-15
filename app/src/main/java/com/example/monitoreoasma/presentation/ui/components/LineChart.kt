package com.example.monitoreoasma.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(datos: List<Float>, etiquetas: List<String>) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            datos.forEach {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp)
                        .background(Color.Red)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            etiquetas.forEach {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
