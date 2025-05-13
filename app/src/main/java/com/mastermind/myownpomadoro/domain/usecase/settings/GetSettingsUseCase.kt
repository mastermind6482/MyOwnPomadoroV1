package com.mastermind.myownpomadoro.domain.usecase.settings

import com.mastermind.myownpomadoro.domain.model.PomodoroSettings
import com.mastermind.myownpomadoro.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения настроек приложения
 */
class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<PomodoroSettings> {
        return settingsRepository.getSettings()
    }
} 