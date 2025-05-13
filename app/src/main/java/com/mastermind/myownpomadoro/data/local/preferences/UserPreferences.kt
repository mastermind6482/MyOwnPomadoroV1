package com.mastermind.myownpomadoro.data.local.preferences

import kotlinx.serialization.Serializable

/**
 * Модель пользовательских настроек
 */
@Serializable
data class UserPreferences(
    // Настройки таймера
    val workDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val pomodorosUntilLongBreak: Int = 4,
    
    // Настройки автоматизации
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoros: Boolean = false,
    
    // Настройки уведомлений
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    
    // Настройки интерфейса
    val darkThemeEnabled: Boolean = false,
    val useSystemTheme: Boolean = true,
    val keepScreenOn: Boolean = true
) 