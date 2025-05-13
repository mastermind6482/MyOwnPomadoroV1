package com.mastermind.myownpomadoro.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Создаем расширение для Context для доступа к DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Менеджер для работы с пользовательскими настройками через DataStore
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    private val context: Context
) {
    // Ключи для доступа к настройкам
    companion object {
        // Настройки таймера
        val WORK_DURATION = intPreferencesKey("work_duration")
        val SHORT_BREAK_DURATION = intPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration")
        val POMODOROS_UNTIL_LONG_BREAK = intPreferencesKey("pomodoros_until_long_break")
        
        // Настройки автоматизации
        val AUTO_START_BREAKS = booleanPreferencesKey("auto_start_breaks")
        val AUTO_START_POMODOROS = booleanPreferencesKey("auto_start_pomodoros")
        
        // Настройки уведомлений
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        
        // Настройки интерфейса
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
    }
    
    /**
     * Получить текущие настройки пользователя
     */
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            // Настройки таймера
            workDurationMinutes = preferences[WORK_DURATION] ?: 25,
            shortBreakDurationMinutes = preferences[SHORT_BREAK_DURATION] ?: 5,
            longBreakDurationMinutes = preferences[LONG_BREAK_DURATION] ?: 15,
            pomodorosUntilLongBreak = preferences[POMODOROS_UNTIL_LONG_BREAK] ?: 4,
            
            // Настройки автоматизации
            autoStartBreaks = preferences[AUTO_START_BREAKS] ?: false,
            autoStartPomodoros = preferences[AUTO_START_POMODOROS] ?: false,
            
            // Настройки уведомлений
            soundEnabled = preferences[SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[VIBRATION_ENABLED] ?: true,
            
            // Настройки интерфейса
            darkThemeEnabled = preferences[DARK_THEME_ENABLED] ?: false,
            useSystemTheme = preferences[USE_SYSTEM_THEME] ?: true,
            keepScreenOn = preferences[KEEP_SCREEN_ON] ?: true
        )
    }
    
    /**
     * Обновить настройки таймера
     */
    suspend fun updateTimerSettings(
        workDurationMinutes: Int,
        shortBreakDurationMinutes: Int,
        longBreakDurationMinutes: Int,
        pomodorosUntilLongBreak: Int
    ) {
        context.dataStore.edit { preferences ->
            preferences[WORK_DURATION] = workDurationMinutes
            preferences[SHORT_BREAK_DURATION] = shortBreakDurationMinutes
            preferences[LONG_BREAK_DURATION] = longBreakDurationMinutes
            preferences[POMODOROS_UNTIL_LONG_BREAK] = pomodorosUntilLongBreak
        }
    }
    
    /**
     * Обновить настройки автоматизации
     */
    suspend fun updateAutomationSettings(
        autoStartBreaks: Boolean,
        autoStartPomodoros: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_START_BREAKS] = autoStartBreaks
            preferences[AUTO_START_POMODOROS] = autoStartPomodoros
        }
    }
    
    /**
     * Обновить настройки уведомлений
     */
    suspend fun updateNotificationSettings(
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = soundEnabled
            preferences[VIBRATION_ENABLED] = vibrationEnabled
        }
    }
    
    /**
     * Обновить настройки интерфейса
     */
    suspend fun updateInterfaceSettings(
        darkThemeEnabled: Boolean,
        useSystemTheme: Boolean,
        keepScreenOn: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_ENABLED] = darkThemeEnabled
            preferences[USE_SYSTEM_THEME] = useSystemTheme
            preferences[KEEP_SCREEN_ON] = keepScreenOn
        }
    }
    
    /**
     * Обновить отдельную настройку по ключу
     */
    suspend fun <T> updateSetting(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
} 