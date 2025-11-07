package com.example.monitoreoasma.presentation.navigation

import android.net.Uri
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monitoreoasma.presentation.ui.history.HistorialScreen
import com.example.monitoreoasma.presentation.ui.home.HomeScreen
import com.example.monitoreoasma.presentation.ui.login.LoginScreen
import com.example.monitoreoasma.presentation.ui.recomendations.RecomendacionesMedicasScreen
import com.example.monitoreoasma.presentation.ui.test.AudioRecordingScreen
import com.example.monitoreoasma.presentation.ui.test.GenerandoReporteScreen
import com.example.monitoreoasma.presentation.ui.test.IngresoSintomasScreen
import com.example.monitoreoasma.presentation.ui.test.ResultadosScreen
import com.example.monitoreoasma.presentation.ui.test.TestIntroScreen
import com.example.monitoreoasma.presentation.ui.test.VerificacionAudioScreen
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect


@Composable
fun AppNavGraph(
    navController: NavHostController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope() // ✅ Esto debe estar dentro de la función Composable

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            com.example.monitoreoasma.presentation.ui.SplashScreen(
                onGoHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onGoLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            val vm: com.example.monitoreoasma.presentation.viewmodel.LoginViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel()

            val isLoading by vm.isLoading.collectAsState()
            val errorMsg by vm.errorMsg.collectAsState()

            // Puedes mostrar un snackbar/dialog con errorMsg si quieres, por ahora nos basta con el flujo.

            LoginScreen(
                isLoading = isLoading,
                onLoginClick = { email, password ->
                    vm.doLogin(email, password) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onRegisterClick = { /* opcional */ },
                errorMsg = errorMsg // <--- agrega este
            )
        }

        composable("home") {
            val sessionVm: com.example.monitoreoasma.presentation.viewmodel.SessionViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel()
            val userName by sessionVm.userName.collectAsState()
            val scope = rememberCoroutineScope()

            HomeScreen(
                userName = userName,
                onNavigateTo = { navController.navigate(it) },
                onOpenDrawer = { scope.launch { drawerState.open() } }  // <--- abre el drawer raíz
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
                onEnviarClick = { navController.navigate("test/sintomas") }
            )
        }

        composable("test/sintomas") {
            IngresoSintomasScreen(
                drawerState = drawerState,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onGuardarClick = { tos, disnea, inhalador ->
                    // Aquí podrías guardar temporalmente los datos
                    navController.navigate("test/generando")
                }
            )
        }

        composable("test/generando") {
            GenerandoReporteScreen(
                drawerState = drawerState,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onFinalizado = { navController.navigate("test/resultados") }
            )
        }

        composable("test/resultados") {
            ResultadosScreen(
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onVerHistorialClick = { navController.navigate("historial") }
            )
        }

        composable("historial") {
            HistorialScreen(
                onOpenDrawer = { scope.launch { drawerState.open() } }
            )
        }

        composable("recomendaciones") {
            RecomendacionesMedicasScreen(
                drawerState = drawerState,
                onOpenDrawer = { scope.launch { drawerState.open() } }
            )
        }



        // Agrega otras pantallas aquí (historial, perfil, etc.)
    }
}
