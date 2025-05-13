package com.mastermind.myownpomadoro.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastermind.myownpomadoro.domain.model.PomodoroSettings
import com.mastermind.myownpomadoro.domain.repository.SettingsRepository
import com.mastermind.myownpomadoro.domain.usecase.settings.GetSettingsUseCase
import com.mastermind.myownpomadoro.domain.usecase.settings.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val uiState: StateFlow<PomodoroSettings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PomodoroSettings()
        )
    
    fun updateWorkDuration(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.updateWorkDuration(minutes)
        }
    }
    
    fun updateShortBreakDuration(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.updateShortBreakDuration(minutes)
        }
    }
    
    fun updateLongBreakDuration(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.updateLongBreakDuration(minutes)
        }
    }
    
    fun updatePeriodsUntilLongBreak(periods: Int) {
        viewModelScope.launch {
            settingsRepository.updatePeriodsUntilLongBreak(periods)
        }
    }
    
    fun updateAutoStartBreaks(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAutoStartBreaks(enabled)
        }
    }
    
    fun updateAutoStartPomodoros(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAutoStartPomodoros(enabled)
        }
    }
    
    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSoundEnabled(enabled)
        }
    }
    
    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateVibrationEnabled(enabled)
        }
    }
    
    fun updateSoundUri(uri: String) {
        viewModelScope.launch {
            settingsRepository.updateSoundUri(uri)
        }
    }
    
    fun updateDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDarkTheme(enabled)
        }
    }
    
    fun updateUseSystemTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateUseSystemTheme(enabled)
        }
    }
    
    fun updateKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateKeepScreenOn(enabled)
        }
    }
    
    fun updateLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(language)
        }
    }
} 