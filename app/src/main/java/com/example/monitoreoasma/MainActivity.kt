package com.example.monitoreoasma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import com.example.monitoreoasma.presentation.navigation.AppNavGraph
import com.example.monitoreoasma.presentation.ui.home.HomeDrawerContent
import com.example.monitoreoasma.ui.theme.MonitoreoAsmaTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonitoreoAsmaTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navController = rememberNavController()
                val sessionVm: com.example.monitoreoasma.presentation.viewmodel.SessionViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                val userName by sessionVm.userName.collectAsState()
                val scope = rememberCoroutineScope()


                ModalNavigationDrawer(
                    drawerContent = {
                        HomeDrawerContent(
                            userName = userName,
                            onOptionSelected = { route ->
                                scope.launch {
                                    drawerState.close()              // ← cierra el menú
                                    if (navController.currentDestination?.route != route) {
                                        navController.navigate(route)  // ← navega luego de cerrar
                                    }
                                }
                            }
                        )
                    },
                    drawerState = drawerState
                ) {
                    AppNavGraph(
                        navController = navController,
                        drawerState = drawerState
                    )
                }
            }
        }
    }
}
