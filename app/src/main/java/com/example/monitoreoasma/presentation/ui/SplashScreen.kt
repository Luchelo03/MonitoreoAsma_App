package com.example.monitoreoasma.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitoreoasma.presentation.viewmodel.SessionViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun SplashScreen(
    onGoHome: () -> Unit,
    onGoLogin: () -> Unit
) {
    val vm: SessionViewModel = viewModel()
    val isValid by vm.isSessionValid.collectAsState()

    LaunchedEffect(Unit) {
        val valid = vm.readSessionValidOnce()
        if (valid) onGoHome() else onGoLogin()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
