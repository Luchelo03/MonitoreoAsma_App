package com.example.monitoreoasma.data.remote.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

private const val BASE_URL = "https://59167af7ca43.ngrok-free.app"//ACÁ REEMPLAZA LA URL DE NGROK

object ApiClient {

    suspend fun getAirQualityData(): JSONObject? {
        return withContext(Dispatchers.IO) {
            val url = URL("$BASE_URL/api/calidad-aire")
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

    suspend fun login(email: String, password: String): Result<com.example.monitoreoasma.data.remote.dto.LoginResponse> {
        return withContext(Dispatchers.IO) {
            val url = URL("$BASE_URL/api/auth/login")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 8000
                readTimeout = 8000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }

            try {
                val body = """{"email":"$email","password":"$password"}"""
                connection.outputStream.use { it.write(body.toByteArray()) }

                val code = connection.responseCode
                val text = (if (code in 200..299) connection.inputStream else connection.errorStream)
                    ?.bufferedReader()?.use { it.readText() }.orEmpty()

                Log.d("Login", "HTTP code=$code, resp=${text.take(200)}")
                if (code == HttpURLConnection.HTTP_OK) {
                    val json = org.json.JSONObject(text)
                    val user = json.getJSONObject("user")
                    val resp = com.example.monitoreoasma.data.remote.dto.LoginResponse(
                        access_token = json.getString("access_token"),
                        expires_in = json.getLong("expires_in"),
                        token_type = json.getString("token_type"),
                        user = com.example.monitoreoasma.data.remote.dto.LoginUser(
                            user_id = user.getString("user_id"),
                            email = user.getString("email"),
                            name = user.getString("name"),
                            role = user.getString("role")
                        )
                    )
                    Result.success(resp)
                    return@withContext Result.success(resp)
                } else {
                    // Loguea para ver qué devolvió el backend
                    Log.e("Login", "Login failed, code=$code, body=${text.take(500)}")
                    return@withContext Result.failure(RuntimeException(text.ifBlank { "login_error_$code" }))
                }
            } catch (e: Exception) {
                Log.e("Login", "Exception doing login", e)
                return@withContext Result.failure(e)
            } finally {
                connection.disconnect()
            }
        }
    }
}
