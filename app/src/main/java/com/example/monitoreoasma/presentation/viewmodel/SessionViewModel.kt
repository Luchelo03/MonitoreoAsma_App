package com.example.monitoreoasma.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreoasma.data.local.preferences.SessionStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class SessionViewModel(app: Application) : AndroidViewModel(app) {
    private val store = SessionStore(app)

    // Nombre observable
    val userName = store.userNameFlow
        .map { it ?: "Usuario" } // valor por defecto
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Usuario")

    val isSessionValid: StateFlow<Boolean> = store.isSessionValidFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    suspend fun readSessionValidOnce(): Boolean =
        isSessionValid.value || // si ya llegó el valor
                store.isSessionValidFlow.first() // espera la primera emisión real
}