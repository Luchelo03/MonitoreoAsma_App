package com.example.monitoreoasma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.navigation.compose.rememberNavController
import com.example.monitoreoasma.presentation.navigation.AppNavGraph
import com.example.monitoreoasma.presentation.ui.home.HomeDrawerContent
import com.example.monitoreoasma.ui.theme.MonitoreoAsmaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonitoreoAsmaTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navController = rememberNavController()

                ModalNavigationDrawer(
                    drawerContent = {
                        HomeDrawerContent(
                            userName = "Juanito",
                            onOptionSelected = { navController.navigate(it) }
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
