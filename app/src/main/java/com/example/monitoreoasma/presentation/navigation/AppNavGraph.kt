package com.example.monitoreoasma.presentation.navigation

import android.net.Uri
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.monitoreoasma.presentation.ui.SplashScreen
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
import com.example.monitoreoasma.presentation.viewmodel.ChildrenViewModel
import com.example.monitoreoasma.presentation.viewmodel.LoginViewModel
import com.example.monitoreoasma.presentation.viewmodel.SessionViewModel
import com.example.monitoreoasma.presentation.viewmodel.TestSessionViewModel
import com.example.monitoreoasma.presentation.viewmodel.UploadAudioViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

@Composable
fun AppNavGraph(
    navController: NavHostController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = "splash") {

        // SPLASH
        composable("splash") {
            SplashScreen(
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

        // LOGIN
        composable("login") {
            val vm: LoginViewModel = viewModel()
            val isLoading by vm.isLoading.collectAsState()
            val errorMsg by vm.errorMsg.collectAsState()

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
                errorMsg = errorMsg
            )
        }

        // HOME
        composable("home") {
            val sessionVm: SessionViewModel = viewModel()
            val userName by sessionVm.userName.collectAsState()
            val innerScope = rememberCoroutineScope()

            HomeScreen(
                userName = userName,
                onNavigateTo = { navController.navigate(it) },
                onOpenDrawer = { innerScope.launch { drawerState.open() } }
            )
        }

        // TEST INTRO
        composable("test") {
            TestIntroScreen(
                drawerState = drawerState,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onGrabarClick = { navController.navigate("test/audio") }
            )
        }

        // TEST AUDIO (grabación)
        composable("test/audio") {
            AudioRecordingScreen(
                onRecordingFinished = { audioPath ->
                    println("DEBUG → NAV to verificacion with: $audioPath")
                    val encoded = Uri.encode(audioPath)
                    navController.navigate("test/verificacion/$encoded")
                },
                onOpenDrawer = { scope.launch { drawerState.open() } }
            )
        }

        // VERIFICACIÓN AUDIO
        composable(
            route = "test/verificacion/{audioPath}",
            arguments = listOf(
                navArgument("audioPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val raw = backStackEntry.arguments?.getString("audioPath")
            val audioPath = raw?.let { Uri.decode(it) } ?: ""

            println("DEBUG → Llegó a verificacion: $audioPath")

            VerificacionAudioScreen(
                audioFilePath = audioPath,
                drawerState = drawerState,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onRepetirClick = { navController.navigate("test/audio") },
                onEnviarClick = { path ->
                    println("DEBUG → Enviar desde verificacion: $path")
                    val encoded = Uri.encode(path)
                    navController.navigate("test/sintomas/$encoded")
                }
            )
        }

        // SÍNTOMAS
        composable(
            route = "test/sintomas/{audioPath}",
            arguments = listOf(
                navArgument("audioPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val raw = backStackEntry.arguments?.getString("audioPath")
            val audioPath = raw?.let { Uri.decode(it) } ?: ""

            println("DEBUG → Llegó a síntomas: $audioPath")

            // ViewModel compartido para todo el flujo de test
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("test")
            }
            val testVm: TestSessionViewModel = viewModel(parentEntry)

            IngresoSintomasScreen(
                drawerState = drawerState,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                audioPath = audioPath,
                onGuardarClick = { tos, disnea, inh, path ->
                    println("DEBUG → Guardar síntomas: path=$path")

                    testVm.audioPath = path
                    testVm.tos = tos
                    testVm.disnea = disnea
                    testVm.usoInhalador = inh

                    navController.navigate("test/generando")
                }
            )
        }

        // GENERANDO REPORTE (subida al backend)
        composable("test/generando") {
            val parentEntry = remember {
                navController.getBackStackEntry("test")
            }
            val testVm: TestSessionViewModel = viewModel(parentEntry)
            val childrenVm: ChildrenViewModel = viewModel()
            val uploadVm: UploadAudioViewModel = viewModel()

            val childIdState by childrenVm.childId.collectAsState()
            val loading by uploadVm.loading.collectAsState()
            val error by uploadVm.error.collectAsState()
            val result by uploadVm.result.collectAsState()

            // Lanzar la subida cuando tengamos childId y audioPath
            LaunchedEffect(childIdState, testVm.audioPath) {
                val childId = childIdState
                val path = testVm.audioPath

                println("DEBUG → childId=$childId  audioPath=$path")

                if (childId.isNullOrBlank()) {
                    println("DEBUG → childId aún no está listo")
                    return@LaunchedEffect
                }

                if (path.isBlank()) {
                    println("DEBUG → audioPath está vacío")
                    return@LaunchedEffect
                }

                val audioFile = File(path)
                println("DEBUG → audioFileExists=${audioFile.exists()} path=$path")

                if (!audioFile.exists()) {
                    println("DEBUG → Archivo no existe, no puedo subirlo")
                    return@LaunchedEffect
                }

                if (!loading && result == null) {
                    println("DEBUG → Ejecutando upload...")
                    uploadVm.upload(
                        childId = childId,
                        audioFile = audioFile,
                        checklistJson = """
                            {
                                "tos": ${testVm.tos},
                                "disnea": ${testVm.disnea},
                                "inhalador": ${testVm.usoInhalador}
                            }
                        """.trimIndent()
                    )
                }
            }

            // UI según estado
            when {
                loading -> {
                    println("DEBUG → UI estado = LOADING")
                    GenerandoReporteScreen(
                        drawerState = drawerState,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onFinalizado = { /* no se usa, navegamos cuando haya result */ }
                    )
                }

                error != null -> {
                    println("DEBUG → UI estado = ERROR: $error")
                    // Puedes hacer una pantalla de error mejor luego
                    GenerandoReporteScreen(
                        drawerState = drawerState,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onFinalizado = { /* sin usar */ }
                    )
                }

                result != null -> {
                    println("DEBUG → UI estado = RESULT listo, navegando a resultados")
                    val encoded = Uri.encode(result.toString())
                    LaunchedEffect("nav_to_result_$encoded") {
                        navController.navigate("test/resultados/$encoded") {
                            popUpTo("test/generando") { inclusive = true }
                        }
                    }

                    GenerandoReporteScreen(
                        drawerState = drawerState,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onFinalizado = { /* sin usar */ }
                    )
                }

                else -> {
                    println("DEBUG → UI estado = IDLE (sin loading, sin error, sin result)")
                    GenerandoReporteScreen(
                        drawerState = drawerState,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onFinalizado = { /* sin usar */ }
                    )
                }
            }
        }

        // RESULTADOS (adaptado al JSON REAL: solo datos de audio)
        composable(
            route = "test/resultados/{resultJson}",
            arguments = listOf(
                navArgument("resultJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val jsonStr = Uri.decode(backStackEntry.arguments?.getString("resultJson") ?: "")
            val json = JSONObject(jsonStr)

            // El backend actual devuelve algo como:
            // {
            //   "recording_label": "Wheeze",
            //   "positive_windows": 18,
            //   "evidence_seconds": 9.5,
            //   "windows": [{ "score": 0.79, ...}, ...],
            //   ...
            // }

            val label = json.optString("recording_label", "Desconocido")
            val positiveWindows = json.optInt("positive_windows", 0)
            val evidenceSeconds = json.optDouble("evidence_seconds", 0.0)

            val windowsArr = json.optJSONArray("windows")
            var maxScore = 0.0
            if (windowsArr != null) {
                for (i in 0 until windowsArr.length()) {
                    val w = windowsArr.getJSONObject(i)
                    val sc = w.optDouble("score", 0.0)
                    if (sc > maxScore) maxScore = sc
                }
            }

            // Riesgo "falso" derivado del label del audio
            val riesgo = if (label == "Wheeze") "alto" else "bajo"
            val score = maxScore
            val aqi = 0 // Por ahora, no viene en el JSON de este endpoint

            ResultadosScreen(
                riesgo = riesgo,
                score = score,
                label = label,
                positiveWindows = positiveWindows,
                evidenceSeconds = evidenceSeconds,
                aqi = aqi,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onVerHistorialClick = { navController.navigate("historial") }
            )
        }

        // HISTORIAL
        composable("historial") {
            HistorialScreen(
                onOpenDrawer = { scope.launch { drawerState.open() } }
            )
        }

        // RECOMENDACIONES
        composable("recomendaciones") {
            RecomendacionesMedicasScreen(
                drawerState = drawerState,
                onOpenDrawer = { scope.launch { drawerState.open() } }
            )
        }
    }
}
