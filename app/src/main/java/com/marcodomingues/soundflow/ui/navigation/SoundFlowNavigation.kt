package com.marcodomingues.soundflow.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.marcodomingues.soundflow.ui.screens.history.HistoryScreen
import com.marcodomingues.soundflow.ui.screens.monitor.MonitorScreen
import com.marcodomingues.soundflow.ui.screens.settings.SettingsScreen

/**
 * Navigation item definition with route and UI properties.
 * Uses string-based routes for simplicity (type-safe routes reserved for complex navigation).
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Monitor : Screen("monitor", "Monitor", Icons.Filled.Mic)
    data object History : Screen("history", "History", Icons.Filled.BarChart)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

/**
 * Main navigation component with BottomNavigationBar.
 * Implements bottom navigation pattern for 3 primary destinations.
 */
@Composable
fun SoundFlowNavigation() {
    val navController = rememberNavController()
    val items = listOf(Screen.Monitor, Screen.History, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Monitor.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Monitor.route) { MonitorScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}