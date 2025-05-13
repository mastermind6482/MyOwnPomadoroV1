package com.mastermind.myownpomadoro.domain.model

/**
 * Модель для хранения пользовательских настроек приложения
 */
data class PomodoroSettings(
    val workDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val periodsUntilLongBreak: Int = 4,
    val autoStartBreaks: Boolean = true,
    val autoStartPomodoros: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    // URI для звука уведомления. Может быть пользовательским файлом
    // или системным звуком из android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
    val soundUri: String = "android.resource://com.mastermind.myownpomadoro/raw/bell",
    val isDarkThemeEnabled: Boolean = false,
    val useSystemTheme: Boolean = true,
    val keepScreenOn: Boolean = true,
    val language: String = "system" // "system", "en", "ru"
) 