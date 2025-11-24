package com.example.monitoreoasma.data.remote.api

import android.util.Log
import com.example.monitoreoasma.data.remote.dto.MeChild
import com.example.monitoreoasma.data.remote.dto.MeResponse
import com.example.monitoreoasma.data.remote.dto.MeUser
import com.example.monitoreoasma.data.remote.dto.TestRunsResponse
import com.example.monitoreoasma.data.remote.dto.TestRunItem
import com.example.monitoreoasma.data.remote.dto.RiskSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

private const val BASE_URL = "https://5e136e704cc5.ngrok-free.app"//ACÁ REEMPLAZA LA URL DE NGROK

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

    suspend fun getMe(token: String): Result<MeResponse> = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL/api/me")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
            setRequestProperty("Authorization", "Bearer $token")
        }
        try {
            val code = conn.responseCode
            val body = (if (code in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.use { it.readText() }.orEmpty()

            Log.d("ME", "code=$code bodyPreview=${body.take(200)}")

            if (code == HttpURLConnection.HTTP_OK) {
                val json = JSONObject(body)
                val userObj = json.getJSONObject("user")
                val user = MeUser(user_id = userObj.getString("user_id"))

                val childrenArr = json.getJSONArray("children")
                val children = buildList {
                    for (i in 0 until childrenArr.length()) {
                        val c = childrenArr.getJSONObject(i)
                        add(
                            MeChild(
                                id = c.getString("id"),
                                name = c.getString("name"),
                                birthdate = c.optString("birthdate", null),
                                sex = c.optString("sex", null),
                                diagnosed_asthma = if (c.has("diagnosed_asthma") && !c.isNull("diagnosed_asthma")) c.getBoolean("diagnosed_asthma") else null,
                                diagnosis_date = c.optString("diagnosis_date", null),
                                started_app_at = c.optString("started_app_at", null),
                                created_at = c.optString("created_at", null)
                            )
                        )
                    }
                }

                Result.success(MeResponse(user = user, children = children))
            } else {
                Result.failure(RuntimeException(body.ifBlank { "me_error_$code" }))
            }
        } catch (e: Exception) {
            Log.e("ME", "Exception", e)
            Result.failure(e)
        } finally {
            conn.disconnect()
        }
    }

    suspend fun getTestRuns(
        token: String,
        childId: String,
        limit: Int,
        offset: Int
    ): Result<TestRunsResponse> = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL/api/children/$childId/test-runs?limit=$limit&offset=$offset")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
            setRequestProperty("Authorization", "Bearer $token")
        }
        try {
            val code = conn.responseCode
            val body = (if (code in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.use { it.readText() }.orEmpty()

            android.util.Log.d("TEST_RUNS", "code=$code bodyPreview=${body.take(200)}")

            if (code == HttpURLConnection.HTTP_OK) {
                val json = org.json.JSONObject(body)
                val itemsJson = json.getJSONArray("items")
                val items = buildList {
                    for (i in 0 until itemsJson.length()) {
                        val itJ = itemsJson.getJSONObject(i)
                        val riskJ = itJ.getJSONObject("risk")
                        add(
                            TestRunItem(
                                test_run_id = itJ.getString("test_run_id"),
                                started_at = itJ.getString("started_at"),
                                finished_at = itJ.optString("finished_at", null),
                                risk = RiskSummary(
                                    level = riskJ.getString("level"),
                                    score = riskJ.getDouble("score")
                                )
                            )
                        )
                    }
                }
                Result.success(
                    TestRunsResponse(
                        child_id = json.getString("child_id"),
                        limit = json.getInt("limit"),
                        offset = json.getInt("offset"),
                        items = items
                    )
                )
            } else {
                Result.failure(RuntimeException(body.ifBlank { "test_runs_error_$code" }))
            }
        } catch (e: Exception) {
            android.util.Log.e("TEST_RUNS", "Exception", e)
            Result.failure(e)
        } finally {
            conn.disconnect()
        }
    }
}
