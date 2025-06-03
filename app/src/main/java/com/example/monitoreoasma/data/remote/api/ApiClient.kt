package com.example.monitoreoasma.data.remote.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {

    suspend fun getAirQualityData(): JSONObject? {
        return withContext(Dispatchers.IO) {
            val url = URL("http://10.0.2.2:5000/api/calidad-aire")
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val stream = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = stream.readText()
                    JSONObject(response)
                } else null
            } finally {
                connection.disconnect()
            }
        }
    }
}
