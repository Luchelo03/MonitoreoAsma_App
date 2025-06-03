package com.example.monitoreoasma.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreoasma.data.remote.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel : ViewModel() {

    private val _airData = MutableStateFlow<JSONObject?>(null)
    val airData = _airData.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow(false)
    val error = _error.asStateFlow()

    init {
        getData()
    }

    private fun getData() {
        _loading.value = true
        _error.value = false
        viewModelScope.launch {
            try {
                val result = ApiClient.getAirQualityData()
                _airData.value = result
            } catch (e: Exception) {
                _error.value = true
            } finally {
                _loading.value = false
            }
        }
    }
}
