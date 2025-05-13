package com.mastermind.myownpomadoro.domain.usecase.settings

import com.mastermind.myownpomadoro.domain.model.PomodoroSettings
import com.mastermind.myownpomadoro.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use Case для обновления настроек приложения
 */
class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: PomodoroSettings) {
        settingsRepository.updateSettings(settings)
    }
} 