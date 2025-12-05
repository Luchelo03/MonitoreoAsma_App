package com.example.monitoreoasma.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreoasma.data.local.preferences.SessionStore
import com.example.monitoreoasma.data.remote.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChildrenViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val session = SessionStore(application)

    private val _childId = MutableStateFlow<String?>(null)
    val childId = _childId.asStateFlow()

    init {
        loadChildren()
    }

    private fun loadChildren() {
        viewModelScope.launch {
            val token = session.tokenFlow.first() ?: return@launch

            val meJson = ApiClient.getMe(token) ?: return@launch

            val childrenArr = meJson.getJSONArray("children")
            if (childrenArr.length() > 0) {
                val childObj = childrenArr.getJSONObject(0)
                _childId.value = childObj.getString("id")
            }
        }
    }
}
