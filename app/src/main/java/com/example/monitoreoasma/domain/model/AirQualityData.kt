package com.example.monitoreoasma.domain.model

data class AirQualityData(
    val aqi: Int,
    val co: Double,
    val humidity: Int,
    val no2: Double,
    val o3: Double,
    val pm10: Int,
    val pm25: Int,
    val so2: Double,
    val temperature: Double,
    val dominentpol: String
)
