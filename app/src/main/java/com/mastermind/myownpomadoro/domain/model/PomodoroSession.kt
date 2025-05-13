package com.mastermind.myownpomadoro.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Модель для хранения информации о завершенной сессии Pomodoro
 */
data class PomodoroSession(
    val id: Long = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val date: LocalDate,
    val durationMinutes: Int,
    val periodType: PeriodType,
    val wasCompleted: Boolean
) 