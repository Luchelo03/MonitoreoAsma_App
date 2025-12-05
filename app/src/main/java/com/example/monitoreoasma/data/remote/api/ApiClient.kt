package com.example.monitoreoasma.data.remote.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

private const val BASE_URL = "https://d7882879fdd9.ngrok-free.app" // tu ngrok actual

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
                    val json = JSONObject(text)
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
                    return@withContext Result.success(resp)
                } else {
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

    suspend fun uploadAudioRecording(
        token: String,
        childId: String,
        audioFile: File,
        checklistJson: String,
        district: String = "SMP"
    ): JSONObject? = withContext(Dispatchers.IO) {
        val boundary = "----AsmaBoundary${System.currentTimeMillis()}"
        val url = URL("$BASE_URL/api/audio/classify-recording")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 8000
            readTimeout = 8000
            doOutput = true
            doInput = true
            setRequestProperty("Authorization", "Bearer $token")
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        }

        val output = DataOutputStream(connection.outputStream)

        fun writeFormField(name: String, value: String) {
            output.writeBytes("--$boundary\r\n")
            output.writeBytes("Content-Disposition: form-data; name=\"$name\"\r\n\r\n")
            output.writeBytes("$value\r\n")
        }

        fun writeFileField(name: String, file: File) {
            output.writeBytes("--$boundary\r\n")
            output.writeBytes(
                "Content-Disposition: form-data; name=\"$name\"; filename=\"${file.name}\"\r\n"
            )
            output.writeBytes("Content-Type: audio/wav\r\n\r\n")

            val bytes = file.readBytes()
            output.write(bytes)
            output.writeBytes("\r\n")
        }

        Log.d("UploadAudio", "POST $url (file=${audioFile.absolutePath})")

        // Campos
        writeFormField("child_id", childId)
        writeFormField("checklist", checklistJson)
        writeFormField("district", district)

        // Archivo WAV
        writeFileField("audio", audioFile)

        // Cerrar body
        output.writeBytes("--$boundary--\r\n")
        output.flush()
        output.close()

        val responseCode = connection.responseCode
        val body = (if (responseCode in 200..299) connection.inputStream else connection.errorStream)
            ?.bufferedReader()?.use { it.readText() }.orEmpty()

        return@withContext if (responseCode in 200..299) {
            Log.d("UploadAudio", "HTTP code=$responseCode, body=${body.take(300)}")
            JSONObject(body)
        } else {
            Log.e("UploadAudio", "Error HTTP code=$responseCode, body=${body.take(300)}")
            null
        }
    }

    suspend fun getMe(token: String): JSONObject? = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL/api/me")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
            setRequestProperty("Authorization", "Bearer $token")
        }

        try {
            val code = connection.responseCode

            val text = (if (code in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader()?.use { it.readText() }
                .orEmpty()

            if (code == HttpURLConnection.HTTP_OK) {
                return@withContext JSONObject(text)
            } else {
                Log.e("ApiClient", "getMe failed, code=$code, body=${text.take(300)}")
                return@withContext null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            connection.disconnect()
        }
    }
}
