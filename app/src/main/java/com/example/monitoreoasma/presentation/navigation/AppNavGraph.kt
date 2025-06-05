package com.example.monitoreoasma.presentation.navigation

import android.net.Uri
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monitoreoasma.presentation.ui.home.HomeScreen
import com.example.monitoreoasma.presentation.ui.login.LoginScreen
import com.example.monitoreoasma.presentation.ui.test.AudioRecordingScreen
import com.example.monitoreoasma.presentation.ui.test.TestIntroScreen
import com.example.monitoreoasma.presentation.ui.test.VerificacionAudioScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope() // ✅ Esto debe estar dentro de la función Composable

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginClick = { navController.navigate("home") },
                isLoading = false
            )
        }

        composable("home") {
            HomeScreen(
                userName = "Juanito",
                onNavigateTo = { navController.navigate(it) }
            )
        }

        composable("test") {
            TestIntroScreen(
                drawerState = drawerState,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onGrabarClick = { navController.navigate("test/audio") }
            )
        }

        composable("test/audio") {
            AudioRecordingScreen(
                onRecordingFinished = { audioPath ->
                    val encodedPath = Uri.encode(audioPath)
                    navController.navigate("test/verificacion/$encodedPath")
                },
                onOpenDrawer = { scope.launch { drawerState.open() } }
            )
        }

        composable("test/verificacion/{audioPath}") { backStackEntry ->
            val audioPath = Uri.decode(backStackEntry.arguments?.getString("audioPath") ?: "")
            VerificacionAudioScreen(
                audioFilePath = audioPath,
                drawerState = drawerState,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onRepetirClick = { navController.navigate("test/audio") },
                onEnviarClick = { navController.navigate("home") }
            )
        }

        // Agrega otras pantallas aquí (historial, perfil, etc.)
    }
}
