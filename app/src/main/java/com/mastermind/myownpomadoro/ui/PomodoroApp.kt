package com.mastermind.myownpomadoro.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mastermind.myownpomadoro.R
import com.mastermind.myownpomadoro.ui.navigation.Screen
import com.mastermind.myownpomadoro.ui.screen.history.HistoryScreen
import com.mastermind.myownpomadoro.ui.screen.settings.SettingsScreen
import com.mastermind.myownpomadoro.ui.screen.statistics.StatisticsScreen
import com.mastermind.myownpomadoro.ui.screen.timer.TimerScreen

@Composable
fun PomodoroApp() {
    val navController = rememberNavController()
    
    val items = listOf(
        Screen.Timer,
        Screen.Statistics,
        Screen.History,
        Screen.Settings
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                painter = painterResource(id = screen.iconResId),
                                contentDescription = stringResource(id = screen.titleResId)
                            )
                        },
                        label = { Text(stringResource(id = screen.titleResId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Избегаем накопления экранов в стеке навигации
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Избегаем создания нескольких копий одного и того же экрана
                                launchSingleTop = true
                                // Восстанавливаем состояние при возвращении к экрану
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
            startDestination = Screen.Timer.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Timer.route) {
                TimerScreen()
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
} 