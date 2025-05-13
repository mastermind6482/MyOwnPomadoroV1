package com.mastermind.myownpomadoro.data.repository

import com.mastermind.myownpomadoro.data.local.preferences.SettingsDataStore
import com.mastermind.myownpomadoro.domain.model.PomodoroSettings
import com.mastermind.myownpomadoro.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override fun getSettings(): Flow<PomodoroSettings> {
        return settingsDataStore.settingsFlow
    }

    override suspend fun updateSettings(settings: PomodoroSettings) {
        settingsDataStore.updateSettings(settings)
    }

    override suspend fun updateWorkDuration(minutes: Int) {
        settingsDataStore.updateWorkDuration(minutes)
    }

    override suspend fun updateShortBreakDuration(minutes: Int) {
        settingsDataStore.updateShortBreakDuration(minutes)
    }

    override suspend fun updateLongBreakDuration(minutes: Int) {
        settingsDataStore.updateLongBreakDuration(minutes)
    }

    override suspend fun updatePeriodsUntilLongBreak(periods: Int) {
        settingsDataStore.updatePeriodsUntilLongBreak(periods)
    }

    override suspend fun updateAutoStartBreaks(enabled: Boolean) {
        settingsDataStore.updateAutoStartBreaks(enabled)
    }

    override suspend fun updateAutoStartPomodoros(enabled: Boolean) {
        settingsDataStore.updateAutoStartPomodoros(enabled)
    }

    override suspend fun updateSoundEnabled(enabled: Boolean) {
        settingsDataStore.updateSoundEnabled(enabled)
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        settingsDataStore.updateVibrationEnabled(enabled)
    }

    override suspend fun updateSoundUri(uri: String) {
        settingsDataStore.updateSoundUri(uri)
    }

    override suspend fun updateDarkTheme(enabled: Boolean) {
        settingsDataStore.updateDarkTheme(enabled)
    }

    override suspend fun updateUseSystemTheme(enabled: Boolean) {
        settingsDataStore.updateUseSystemTheme(enabled)
    }

    override suspend fun updateKeepScreenOn(enabled: Boolean) {
        settingsDataStore.updateKeepScreenOn(enabled)
    }

    override suspend fun updateLanguage(language: String) {
        settingsDataStore.updateLanguage(language)
    }
} 