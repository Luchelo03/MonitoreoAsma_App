package com.example.monitoreoasma.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreoasma.data.local.preferences.SessionStore
import com.example.monitoreoasma.data.remote.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val session = SessionStore(app)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg = _errorMsg.asStateFlow()

    fun doLogin(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _errorMsg.value = null

        viewModelScope.launch {
            val res = ApiClient.login(email, password)
            res.onSuccess { r ->
                val now = System.currentTimeMillis() / 1000L
                session.saveSession(
                    token = r.access_token,
                    tokenType = r.token_type,
                    expiresIn = r.expires_in,
                    loginTimeUtc = now,
                    userId = r.user.user_id,
                    email = r.user.email,
                    name = r.user.name,
                    role = r.user.role
                )
                _isLoading.value = false
                onSuccess()
            }.onFailure { e ->
                _isLoading.value = false
                _errorMsg.value = when {
                    e.message?.contains("unauthorized", true) == true -> "Credenciales inválidas."
                    else -> "No se pudo iniciar sesión. Intenta nuevamente."
                }
            }
        }
    }
}