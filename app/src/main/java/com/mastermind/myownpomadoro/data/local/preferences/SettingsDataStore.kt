package com.mastermind.myownpomadoro.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mastermind.myownpomadoro.domain.model.PomodoroSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Ключи для настроек
    private object PreferencesKeys {
        val WORK_DURATION = intPreferencesKey("work_duration")
        val SHORT_BREAK_DURATION = intPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration")
        val PERIODS_UNTIL_LONG_BREAK = intPreferencesKey("periods_until_long_break")
        val AUTO_START_BREAKS = booleanPreferencesKey("auto_start_breaks")
        val AUTO_START_POMODOROS = booleanPreferencesKey("auto_start_pomodoros")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val SOUND_URI = stringPreferencesKey("sound_uri")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val LANGUAGE = stringPreferencesKey("language")
    }

    // Получение настроек в виде Flow
    val settingsFlow: Flow<PomodoroSettings> = context.dataStore.data.map { preferences ->
        PomodoroSettings(
            workDurationMinutes = preferences[PreferencesKeys.WORK_DURATION] ?: 25,
            shortBreakDurationMinutes = preferences[PreferencesKeys.SHORT_BREAK_DURATION] ?: 5,
            longBreakDurationMinutes = preferences[PreferencesKeys.LONG_BREAK_DURATION] ?: 15,
            periodsUntilLongBreak = preferences[PreferencesKeys.PERIODS_UNTIL_LONG_BREAK] ?: 4,
            autoStartBreaks = preferences[PreferencesKeys.AUTO_START_BREAKS] ?: true,
            autoStartPomodoros = preferences[PreferencesKeys.AUTO_START_POMODOROS] ?: false,
            soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
            soundUri = preferences[PreferencesKeys.SOUND_URI] ?: "android.resource://com.mastermind.myownpomadoro/raw/bell",
            isDarkThemeEnabled = preferences[PreferencesKeys.DARK_THEME] ?: false,
            useSystemTheme = preferences[PreferencesKeys.USE_SYSTEM_THEME] ?: true,
            keepScreenOn = preferences[PreferencesKeys.KEEP_SCREEN_ON] ?: true,
            language = preferences[PreferencesKeys.LANGUAGE] ?: "system"
        )
    }

    // Обновление настроек
    suspend fun updateSettings(settings: PomodoroSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORK_DURATION] = settings.workDurationMinutes
            preferences[PreferencesKeys.SHORT_BREAK_DURATION] = settings.shortBreakDurationMinutes
            preferences[PreferencesKeys.LONG_BREAK_DURATION] = settings.longBreakDurationMinutes
            preferences[PreferencesKeys.PERIODS_UNTIL_LONG_BREAK] = settings.periodsUntilLongBreak
            preferences[PreferencesKeys.AUTO_START_BREAKS] = settings.autoStartBreaks
            preferences[PreferencesKeys.AUTO_START_POMODOROS] = settings.autoStartPomodoros
            preferences[PreferencesKeys.SOUND_ENABLED] = settings.soundEnabled
            preferences[PreferencesKeys.VIBRATION_ENABLED] = settings.vibrationEnabled
            preferences[PreferencesKeys.SOUND_URI] = settings.soundUri
            preferences[PreferencesKeys.DARK_THEME] = settings.isDarkThemeEnabled
            preferences[PreferencesKeys.USE_SYSTEM_THEME] = settings.useSystemTheme
            preferences[PreferencesKeys.KEEP_SCREEN_ON] = settings.keepScreenOn
            preferences[PreferencesKeys.LANGUAGE] = settings.language
        }
    }

    // Методы для обновления отдельных настроек
    suspend fun updateWorkDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORK_DURATION] = minutes
        }
    }

    suspend fun updateShortBreakDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHORT_BREAK_DURATION] = minutes
        }
    }

    suspend fun updateLongBreakDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LONG_BREAK_DURATION] = minutes
        }
    }

    suspend fun updatePeriodsUntilLongBreak(periods: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PERIODS_UNTIL_LONG_BREAK] = periods
        }
    }

    suspend fun updateAutoStartBreaks(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_START_BREAKS] = enabled
        }
    }

    suspend fun updateAutoStartPomodoros(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_START_POMODOROS] = enabled
        }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun updateSoundUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_URI] = uri
        }
    }

    suspend fun updateDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME] = enabled
        }
    }

    suspend fun updateUseSystemTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_SYSTEM_THEME] = enabled
        }
    }

    suspend fun updateKeepScreenOn(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEEP_SCREEN_ON] = enabled
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language
        }
    }
} 