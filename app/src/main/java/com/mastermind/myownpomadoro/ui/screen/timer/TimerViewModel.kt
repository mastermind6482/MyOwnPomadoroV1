package com.mastermind.myownpomadoro.ui.screen.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastermind.myownpomadoro.domain.model.PomodoroState
import com.mastermind.myownpomadoro.domain.repository.TimerRepository
import com.mastermind.myownpomadoro.domain.usecase.timer.GetTimerStateUseCase
import com.mastermind.myownpomadoro.domain.usecase.timer.PauseTimerUseCase
import com.mastermind.myownpomadoro.domain.usecase.timer.ResetTimerUseCase
import com.mastermind.myownpomadoro.domain.usecase.timer.StartTimerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val getTimerStateUseCase: GetTimerStateUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val pauseTimerUseCase: PauseTimerUseCase,
    private val resetTimerUseCase: ResetTimerUseCase,
    private val timerRepository: TimerRepository
) : ViewModel() {
    
    val uiState: StateFlow<PomodoroState> = getTimerStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PomodoroState()
        )
    
    fun startTimer() {
        viewModelScope.launch {
            startTimerUseCase()
        }
    }
    
    fun pauseTimer() {
        viewModelScope.launch {
            pauseTimerUseCase()
        }
    }
    
    fun resetTimer() {
        viewModelScope.launch {
            resetTimerUseCase()
        }
    }
    
    fun skipToNextPeriod() {
        viewModelScope.launch {
            timerRepository.moveToNextPeriod()
        }
    }
} 