package com.example.monitoreoasma.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreoasma.data.local.preferences.SessionStore
import com.example.monitoreoasma.data.remote.api.ApiClient
import com.example.monitoreoasma.data.remote.dto.TestRunItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers

class HistorialViewModel(app: Application) : AndroidViewModel(app) {

    private val session = SessionStore(app)

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _childId = MutableStateFlow<String?>(null)
    val childId: StateFlow<String?> = _childId.asStateFlow()

    private val _items = MutableStateFlow<List<TestRunItem>>(emptyList())
    val items: StateFlow<List<TestRunItem>> = _items.asStateFlow()

    private val pageSize = 20

    private val _offset = MutableStateFlow(0)
    val offset: StateFlow<Int> = _offset.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _loadingMore = MutableStateFlow(false)
    val loadingMore: StateFlow<Boolean> = _loadingMore.asStateFlow()

    /**
     * Carga inicial:
     * - Lee token
     * - Pide /api/me y toma el primer child_id
     * - Pide /api/children/{child}/test-runs?limit=20&offset=0
     */
    fun loadInitial() {
        if (_loading.value) return
        _loading.value = true
        _error.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = session.tokenFlow.first()
                if (token.isNullOrBlank()) {
                    _error.value = "Sesión no válida. Inicia sesión nuevamente."
                    _loading.value = false
                    return@launch
                }

                // 1) /api/me
                val meRes = ApiClient.getMe(token)
                var firstChildId: String? = null
                meRes.onSuccess { me ->
                    firstChildId = me.children.firstOrNull()?.id
                }.onFailure { e ->
                    _error.value = "No se pudo cargar tu perfil: ${e.message}"
                    _loading.value = false
                    return@launch
                }

                if (firstChildId.isNullOrBlank()) {
                    _error.value = "No tienes niños registrados."
                    _loading.value = false
                    return@launch
                }

                _childId.value = firstChildId

                // 2) /api/children/{child}/test-runs
                val runsRes = ApiClient.getTestRuns(
                    token = token,
                    childId = firstChildId!!,
                    limit = pageSize,
                    offset = 0
                )

                runsRes.onSuccess { resp ->
                    // Ordena desc y reemplaza lista
                    val sorted = resp.items.sortedByDescending { it.started_at }
                    _items.value = sorted

                    // reinicia paginación
                    _offset.value = resp.items.size
                    _hasMore.value = resp.items.size >= pageSize

                }.onFailure { e ->
                    _error.value = when {
                        e.message?.contains("403") == true -> "No tienes acceso al historial de este niño."
                        else -> "No se pudo cargar el historial: ${e.message}"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadMore() {
        // Precondiciones
        if (_loading.value || _loadingMore.value) return
        val child = _childId.value ?: return
        if (!_hasMore.value) return

        _loadingMore.value = true
        _error.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = session.tokenFlow.first()
                if (token.isNullOrBlank()) {
                    _error.value = "Sesión no válida. Inicia sesión nuevamente."
                    _loadingMore.value = false
                    return@launch
                }

                val currentOffset = _offset.value
                val runsRes = ApiClient.getTestRuns(
                    token = token,
                    childId = child,
                    limit = pageSize,
                    offset = currentOffset
                )

                runsRes.onSuccess { resp ->
                    // merge sin duplicados
                    val merged = (_items.value + resp.items)
                        .distinctBy { it.test_run_id }
                        .sortedByDescending { it.started_at }

                    _items.value = merged
                    _offset.value = currentOffset + resp.items.size
                    _hasMore.value = resp.items.size >= pageSize
                }.onFailure { e ->
                    _error.value = "No se pudo cargar más: ${e.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
            } finally {
                _loadingMore.value = false
            }
        }
    }
}
