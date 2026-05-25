package com.marcodomingues.noiseguard.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.marcodomingues.noiseguard.ui.screens.history.HistoryScreen
import com.marcodomingues.noiseguard.ui.screens.monitor.MonitorScreen
import com.marcodomingues.noiseguard.ui.screens.settings.SettingsScreen
import com.marcodomingues.noiseguard.ui.theme.NeonColors

@Composable
fun NoiseGuardNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = NeonColors.CyanStart,
        selectedTextColor = NeonColors.CyanStart,
        unselectedIconColor = Color.White.copy(alpha = 0.5f),
        unselectedTextColor = Color.White.copy(alpha = 0.5f),
        indicatorColor = NeonColors.CyanStart.copy(alpha = 0.2f)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1E1E).copy(alpha = 0.95f),
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Mic, "Monitor") },
                    label = { Text("Monitor") },
                    selected = currentRoute == Route.MONITOR,
                    onClick = {
                        navController.navigate(Route.MONITOR) {
                            popUpTo(Route.MONITOR) {
                                inclusive = false
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = navItemColors
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, "History") },
                    label = { Text("History") },
                    selected = currentRoute == Route.HISTORY,
                    onClick = {
                        navController.navigate(Route.HISTORY) {
                            popUpTo(Route.MONITOR) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = navItemColors
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, "Settings") },
                    label = { Text("Settings") },
                    selected = currentRoute == Route.SETTINGS,
                    onClick = {
                        navController.navigate(Route.SETTINGS) {
                            popUpTo(Route.MONITOR) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = navItemColors
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.MONITOR,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Route.MONITOR) {
                MonitorScreen()
            }

            composable(Route.HISTORY) {
                HistoryScreen()
            }

            composable(Route.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}

private object Route {
    const val MONITOR = "monitor"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}
