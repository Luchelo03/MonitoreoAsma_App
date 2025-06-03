package com.example.monitoreoasma.presentation.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AsthmaAttackChart() {
    val mockData = listOf(0, 1, 2, 1, 3, 2, 1) // niveles del 0 al 3
    val maxLevel = 3

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Últimos ataques asmáticos registrados", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
            ) {
                val width = size.width
                val height = size.height

                val spacing = width / (mockData.size - 1)
                val points = mockData.mapIndexed { index, value ->
                    val x = index * spacing
                    val y = height - (value / maxLevel.toFloat()) * height
                    Offset(x, y)
                }

                val path = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AsthmaAttackChartPreview() {
    AsthmaAttackChart()
}
