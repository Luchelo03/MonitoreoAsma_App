package com.example.monitoreoasma.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine

val Context.sessionDataStore by preferencesDataStore(name = "session")

class SessionStore(private val context: Context) {

    // Claves tipadas
    private val KEY_TOKEN = stringPreferencesKey("access_token")
    private val KEY_TOKEN_TYPE = stringPreferencesKey("token_type")
    private val KEY_EXPIRES_IN = longPreferencesKey("expires_in")
    private val KEY_LOGIN_TS = longPreferencesKey("login_time_utc")
    private val KEY_USER_ID = stringPreferencesKey("user_id")
    private val KEY_EMAIL = stringPreferencesKey("email")
    private val KEY_NAME = stringPreferencesKey("name")
    private val KEY_ROLE = stringPreferencesKey("role")

    // Lectura reactiva del token (ejemplo)
    val tokenFlow: Flow<String?> = context.sessionDataStore.data.map { it[KEY_TOKEN] }
    val userNameFlow: Flow<String?> = context.sessionDataStore.data.map { it[KEY_NAME] }


    suspend fun saveSession(
        token: String,
        tokenType: String,
        expiresIn: Long,
        loginTimeUtc: Long,
        userId: String,
        email: String,
        name: String,
        role: String
    ) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_TOKEN_TYPE] = tokenType
            prefs[KEY_EXPIRES_IN] = expiresIn
            prefs[KEY_LOGIN_TS] = loginTimeUtc
            prefs[KEY_USER_ID] = userId
            prefs[KEY_EMAIL] = email
            prefs[KEY_NAME] = name
            prefs[KEY_ROLE] = role
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }

    private fun nowSeconds(): Long = System.currentTimeMillis() / 1000L

    val isSessionValidFlow: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        val token = prefs[KEY_TOKEN]
        val expIn = prefs[KEY_EXPIRES_IN] ?: 0L
        val loginTs = prefs[KEY_LOGIN_TS] ?: 0L
        if (token.isNullOrBlank()) return@map false

        // margen de 5 segundos para evitar bordes
        val expiresAt = loginTs + expIn - 5
        nowSeconds() < expiresAt
    }
}
