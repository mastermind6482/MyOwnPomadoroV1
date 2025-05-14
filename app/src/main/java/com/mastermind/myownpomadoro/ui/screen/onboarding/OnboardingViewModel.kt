package com.mastermind.myownpomadoro.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastermind.myownpomadoro.data.local.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // Поток для отслеживания статуса онбординга
    val onboardingCompletedFlow = settingsDataStore.onboardingCompletedFlow

    // Метод для завершения онбординга
    suspend fun completeOnboarding() {
        settingsDataStore.updateOnboardingCompleted(true)
    }
} 