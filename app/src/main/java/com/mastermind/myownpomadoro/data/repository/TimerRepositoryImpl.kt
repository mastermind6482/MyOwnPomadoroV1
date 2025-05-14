package com.mastermind.myownpomadoro.data.repository

import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.PomodoroState
import com.mastermind.myownpomadoro.domain.model.TimerState
import com.mastermind.myownpomadoro.domain.repository.SettingsRepository
import com.mastermind.myownpomadoro.domain.repository.TimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRepositoryImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : TimerRepository {
    
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    
    private val _timerState = MutableStateFlow(PomodoroState())
    
    init {
        coroutineScope.launch {
            val settings = settingsRepository.getSettings().first()
            _timerState.update { state ->
                state.copy(
                    workDuration = Duration.ofMinutes(settings.workDurationMinutes.toLong()),
                    shortBreakDuration = Duration.ofMinutes(settings.shortBreakDurationMinutes.toLong()),
                    longBreakDuration = Duration.ofMinutes(settings.longBreakDurationMinutes.toLong()),
                    periodsUntilLongBreak = settings.periodsUntilLongBreak,
                    remainingTime = Duration.ofMinutes(settings.workDurationMinutes.toLong()),
                    totalTime = Duration.ofMinutes(settings.workDurationMinutes.toLong())
                )
            }
        }
    }
    
    override fun getTimerState(): Flow<PomodoroState> {
        return _timerState.asStateFlow()
    }
    
    override suspend fun startTimer() {
        _timerState.update { it.copy(timerState = TimerState.RUNNING) }
    }
    
    override suspend fun pauseTimer() {
        _timerState.update { it.copy(timerState = TimerState.PAUSED) }
    }
    
    override suspend fun resetTimer() {
        val currentState = _timerState.value
        val totalTime = when (currentState.currentPeriodType) {
            PeriodType.WORK -> currentState.workDuration
            PeriodType.SHORT_BREAK -> currentState.shortBreakDuration
            PeriodType.LONG_BREAK -> currentState.longBreakDuration
        }
        
        _timerState.update { 
            it.copy(
                timerState = TimerState.IDLE,
                remainingTime = totalTime,
                totalTime = totalTime
            )
        }
    }
    
    override suspend fun updateTimerState(state: TimerState) {
        _timerState.update { it.copy(timerState = state) }
    }
    
    override suspend fun updatePeriodType(periodType: PeriodType) {
        val settings = settingsRepository.getSettings().first()
        val totalTime = when (periodType) {
            PeriodType.WORK -> Duration.ofMinutes(settings.workDurationMinutes.toLong())
            PeriodType.SHORT_BREAK -> Duration.ofMinutes(settings.shortBreakDurationMinutes.toLong())
            PeriodType.LONG_BREAK -> Duration.ofMinutes(settings.longBreakDurationMinutes.toLong())
        }
        
        _timerState.update { 
            it.copy(
                currentPeriodType = periodType,
                remainingTime = totalTime,
                totalTime = totalTime,
                timerState = TimerState.IDLE
            )
        }
    }
    
    override suspend fun updateRemainingTime(time: Duration) {
        _timerState.update { it.copy(remainingTime = time) }
        
        if (time.isZero || time.isNegative) {
            _timerState.update { it.copy(timerState = TimerState.COMPLETED) }
        }
    }
    
    override suspend fun incrementCompletedPeriods() {
        val currentState = _timerState.value
        when (currentState.currentPeriodType) {
            PeriodType.WORK -> {
                _timerState.update { 
                    it.copy(completedWorkPeriods = it.completedWorkPeriods + 1) 
                }
            }
            PeriodType.SHORT_BREAK -> {
                _timerState.update { 
                    it.copy(completedShortBreaks = it.completedShortBreaks + 1) 
                }
            }
            PeriodType.LONG_BREAK -> {
                _timerState.update { 
                    it.copy(completedLongBreaks = it.completedLongBreaks + 1) 
                }
            }
        }
    }
    
    override suspend fun moveToNextPeriod() {
        val currentState = _timerState.value
        val settings = settingsRepository.getSettings().first()
        
        // Определяем следующий тип периода
        val nextPeriodType = when (currentState.currentPeriodType) {
            PeriodType.WORK -> {
                // Если достигли нужного количества рабочих периодов, переходим к длинному перерыву
                if ((currentState.completedWorkPeriods + 1) % settings.periodsUntilLongBreak == 0) {
                    PeriodType.LONG_BREAK
                } else {
                    PeriodType.SHORT_BREAK
                }
            }
            PeriodType.SHORT_BREAK, PeriodType.LONG_BREAK -> PeriodType.WORK
        }
        
        updatePeriodType(nextPeriodType)
        
        // Автоматически запускаем следующий период, если это настроено
        if ((nextPeriodType == PeriodType.WORK && settings.autoStartPomodoros) ||
            (nextPeriodType != PeriodType.WORK && settings.autoStartBreaks)) {
            startTimer()
        }
    }
} 