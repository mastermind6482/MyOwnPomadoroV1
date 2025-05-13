package com.mastermind.myownpomadoro.domain.model

import java.time.Duration

/**
 * Перечисления для типов периодов Pomodoro
 */
enum class PeriodType {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}

/**
 * Перечисления для состояния таймера
 */
enum class TimerState {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

/**
 * Модель для хранения состояния текущей сессии Pomodoro
 */
data class PomodoroState(
    val timerState: TimerState = TimerState.IDLE,
    val currentPeriodType: PeriodType = PeriodType.WORK,
    val remainingTime: Duration = Duration.ofMinutes(25),
    val totalTime: Duration = Duration.ofMinutes(25),
    val completedWorkPeriods: Int = 0,
    val completedShortBreaks: Int = 0,
    val completedLongBreaks: Int = 0,
    val workDuration: Duration = Duration.ofMinutes(25),
    val shortBreakDuration: Duration = Duration.ofMinutes(5),
    val longBreakDuration: Duration = Duration.ofMinutes(15),
    val periodsUntilLongBreak: Int = 4
) 