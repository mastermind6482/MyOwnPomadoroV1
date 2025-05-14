package com.mastermind.myownpomadoro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastermind.myownpomadoro.data.local.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroAppViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // Поток для отслеживания статуса онбординга
    val onboardingCompleted: Flow<Boolean> = settingsDataStore.onboardingCompletedFlow

    // Метод для завершения онбординга
    fun completeOnboarding() {
        viewModelScope.launch {
            settingsDataStore.updateOnboardingCompleted(true)
        }
    }
} 