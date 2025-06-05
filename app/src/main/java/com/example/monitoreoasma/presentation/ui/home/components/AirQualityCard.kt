package com.example.monitoreoasma.presentation.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreoasma.domain.model.AirQualityData

@Composable
fun AirQualityCard(airData: AirQualityData?) {
    val backgroundColor = when (airData?.aqi) {
        in 0..50 -> Color(0xFF009966)
        in 51..100 -> Color(0xFFFFDE33)
        in 101..150 -> Color(0xFFFF9933)
        in 151..200 -> Color(0xFFCC0033)
        in 201..300 -> Color(0xFF660099)
        else -> Color(0xFF7E0023)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (airData != null) {
                when (airData.aqi) {
                    in 0..50 -> Color(0xFF009966)
                    in 51..100 -> Color(0xFFFFDE33)
                    in 101..150 -> Color(0xFFFF9933)
                    in 151..200 -> Color(0xFFCC0033)
                    in 201..300 -> Color(0xFF660099)
                    else -> Color(0xFF7E0023)
                }
            } else {
                Color.LightGray
            }
        )
    ) {
        if (airData != null) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Temp: ${airData.temperature}°C", color = Color.Black)
                    Text("Humedad: ${airData.humidity}%", color = Color.Black)
                    Text("PM2.5: ${airData.pm25} µg/m³", color = Color.Black)
                    Text("PM10: ${airData.pm10} µg/m³", color = Color.Black)
                    Text("CO: ${airData.co} ppm", color = Color.Black)
                }

                Text(
                    text = "AQI\n${airData.aqi}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin conexión al servidor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}

