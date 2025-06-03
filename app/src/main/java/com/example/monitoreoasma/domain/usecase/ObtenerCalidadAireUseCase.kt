package com.example.monitoreoasma.domain.usecase

import com.example.monitoreoasma.domain.model.AirQualityData
import com.example.monitoreoasma.data.remote.api.ApiClient.getAirQualityData

suspend fun obtenerCalidadAireUseCase(): AirQualityData? {
    val json = getAirQualityData() ?: return null

    return AirQualityData(
        aqi = json.getInt("aqi"),
        co = json.getDouble("co"),
        humidity = json.getInt("humidity"),
        no2 = json.getDouble("no2"),
        o3 = json.getDouble("o3"),
        pm10 = json.getInt("pm10"),
        pm25 = json.getInt("pm25"),
        so2 = json.getDouble("so2"),
        temperature = json.getDouble("temperature"),
        dominentpol = json.getString("dominentpol")
    )
}
