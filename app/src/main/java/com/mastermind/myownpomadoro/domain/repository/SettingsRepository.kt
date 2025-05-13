package com.mastermind.myownpomadoro.domain.repository

import com.mastermind.myownpomadoro.domain.model.PomodoroSettings
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с настройками приложения
 */
interface SettingsRepository {
    /**
     * Получить настройки в виде Flow
     */
    fun getSettings(): Flow<PomodoroSettings>
    
    /**
     * Обновить настройки
     */
    suspend fun updateSettings(settings: PomodoroSettings)
    
    /**
     * Обновить отдельную настройку
     */
    suspend fun updateWorkDuration(minutes: Int)
    suspend fun updateShortBreakDuration(minutes: Int)
    suspend fun updateLongBreakDuration(minutes: Int)
    suspend fun updatePeriodsUntilLongBreak(periods: Int)
    suspend fun updateAutoStartBreaks(enabled: Boolean)
    suspend fun updateAutoStartPomodoros(enabled: Boolean)
    suspend fun updateSoundEnabled(enabled: Boolean)
    suspend fun updateVibrationEnabled(enabled: Boolean)
    suspend fun updateSoundUri(uri: String)
    suspend fun updateDarkTheme(enabled: Boolean)
    suspend fun updateUseSystemTheme(enabled: Boolean)
    suspend fun updateKeepScreenOn(enabled: Boolean)
    suspend fun updateLanguage(language: String)
} 