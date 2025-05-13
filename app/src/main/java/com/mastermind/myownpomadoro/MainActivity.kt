package com.mastermind.myownpomadoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mastermind.myownpomadoro.data.local.preferences.SettingsDataStore
import com.mastermind.myownpomadoro.ui.theme.MyOwnPomadoroTheme
import com.mastermind.myownpomadoro.ui.PomodoroApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            // Получаем настройки из SettingsDataStore
            val settings by settingsDataStore.settingsFlow.collectAsState(
                initial = com.mastermind.myownpomadoro.domain.model.PomodoroSettings()
            )
            
            // Применяем темную тему, если она включена в настройках
            MyOwnPomadoroTheme(
                darkTheme = isSystemInDarkTheme(),
                // Используем настройку темы только если не используется системная тема
                forceDarkTheme = if (settings.useSystemTheme) false else settings.isDarkThemeEnabled
            ) {
                // Включаем или отключаем экран в зависимости от настроек
                if (settings.keepScreenOn) {
                    window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PomodoroApp()
                }
            }
        }
    }
}