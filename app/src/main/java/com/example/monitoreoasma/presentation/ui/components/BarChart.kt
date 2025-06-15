package com.example.monitoreoasma.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BarChart(datos: Map<String, Float>) {
    Column {
        datos.forEach { (etiqueta, valor) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(etiqueta, modifier = Modifier.width(100.dp))
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp)
                        .background(Color.Blue)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
