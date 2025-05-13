package com.mastermind.myownpomadoro.domain.repository

import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.PomodoroState
import com.mastermind.myownpomadoro.domain.model.TimerState
import kotlinx.coroutines.flow.Flow
import java.time.Duration

/**
 * Интерфейс репозитория для работы с таймером Pomodoro
 */
interface TimerRepository {
    /**
     * Получить текущее состояние таймера в виде Flow
     */
    fun getTimerState(): Flow<PomodoroState>
    
    /**
     * Запустить таймер
     */
    suspend fun startTimer()
    
    /**
     * Приостановить таймер
     */
    suspend fun pauseTimer()
    
    /**
     * Сбросить таймер
     */
    suspend fun resetTimer()
    
    /**
     * Обновить состояние таймера
     */
    suspend fun updateTimerState(state: TimerState)
    
    /**
     * Обновить тип периода
     */
    suspend fun updatePeriodType(periodType: PeriodType)
    
    /**
     * Обновить оставшееся время
     */
    suspend fun updateRemainingTime(time: Duration)
    
    /**
     * Увеличить счетчик завершенных периодов
     */
    suspend fun incrementCompletedPeriods()
    
    /**
     * Перейти к следующему периоду
     */
    suspend fun moveToNextPeriod()
} 