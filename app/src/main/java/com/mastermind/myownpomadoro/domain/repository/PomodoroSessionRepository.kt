package com.mastermind.myownpomadoro.domain.repository

import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Интерфейс репозитория для работы с сессиями Pomodoro
 */
interface PomodoroSessionRepository {
    /**
     * Получить все сессии в виде Flow
     */
    fun getSessions(): Flow<List<PomodoroSession>>
    
    /**
     * Получить сессии за указанную дату
     */
    fun getSessionsByDate(date: LocalDate): Flow<List<PomodoroSession>>
    
    /**
     * Получить сессии за указанный период
     */
    fun getSessionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<PomodoroSession>>
    
    /**
     * Получить сессии определенного типа
     */
    fun getSessionsByType(periodType: PeriodType): Flow<List<PomodoroSession>>
    
    /**
     * Добавить новую сессию
     */
    suspend fun addSession(session: PomodoroSession): Long
    
    /**
     * Удалить сессию
     */
    suspend fun deleteSession(session: PomodoroSession)
    
    /**
     * Получить статистику по дням
     */
    fun getDailyStats(): Flow<Map<LocalDate, Int>>
    
    /**
     * Экспортировать данные сессий
     */
    suspend fun exportSessions(): String
    
    /**
     * Экспортировать данные отдельной сессии по ID
     */
    suspend fun exportSession(sessionId: Long): String
} 