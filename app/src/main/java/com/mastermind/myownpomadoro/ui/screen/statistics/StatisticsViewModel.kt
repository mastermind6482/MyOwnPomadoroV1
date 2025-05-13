package com.mastermind.myownpomadoro.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import com.mastermind.myownpomadoro.domain.usecase.session.GetDailyStatsUseCase
import com.mastermind.myownpomadoro.domain.usecase.session.GetSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt

data class StatisticsUiState(
    val totalSessions: Int = 0,
    val totalWorkSessions: Int = 0,
    val totalWorkMinutes: Int = 0,
    val completionRate: Int = 0,
    val dailyAverage: Double = 0.0,
    val workSessionsCount: Int = 0,
    val shortBreakSessionsCount: Int = 0,
    val longBreakSessionsCount: Int = 0,
    val workSessionsPercentage: Int = 0,
    val shortBreakSessionsPercentage: Int = 0,
    val longBreakSessionsPercentage: Int = 0,
    val dailyStats: Map<String, Int> = emptyMap()
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getSessionsUseCase: GetSessionsUseCase,
    getDailyStatsUseCase: GetDailyStatsUseCase
) : ViewModel() {
    
    val uiState: StateFlow<StatisticsUiState> = combine(
        getSessionsUseCase(),
        getDailyStatsUseCase()
    ) { sessions, dailyStats ->
        // Общее количество сессий
        val totalSessions = sessions.size
        
        // Группируем сессии по типу
        val sessionsByType = sessions.groupBy { it.periodType }
        
        // Статистика по рабочим сессиям
        val workSessions = sessionsByType[PeriodType.WORK] ?: emptyList()
        val totalWorkSessions = workSessions.size
        val totalWorkMinutes = workSessions.sumOf { it.durationMinutes }
        val completedWorkSessions = workSessions.count { it.wasCompleted }
        val completionRate = if (totalWorkSessions > 0) {
            ((completedWorkSessions.toDouble() / totalWorkSessions) * 100).roundToInt()
        } else {
            0
        }
        
        // Статистика по перерывам
        val shortBreakSessions = sessionsByType[PeriodType.SHORT_BREAK] ?: emptyList()
        val longBreakSessions = sessionsByType[PeriodType.LONG_BREAK] ?: emptyList()
        
        // Процентное соотношение типов сессий
        val workSessionsPercentage = if (totalSessions > 0) {
            ((workSessions.size.toDouble() / totalSessions) * 100).roundToInt()
        } else {
            0
        }
        
        val shortBreakSessionsPercentage = if (totalSessions > 0) {
            ((shortBreakSessions.size.toDouble() / totalSessions) * 100).roundToInt()
        } else {
            0
        }
        
        val longBreakSessionsPercentage = if (totalSessions > 0) {
            ((longBreakSessions.size.toDouble() / totalSessions) * 100).roundToInt()
        } else {
            0
        }
        
        // Среднее количество помидоров в день
        val dailyAverage = if (dailyStats.isNotEmpty()) {
            dailyStats.values.average()
        } else {
            0.0
        }
        
        // Форматируем данные для графика
        val formatter = DateTimeFormatter.ofPattern("dd.MM")
        val formattedDailyStats = dailyStats
            .entries
            .sortedBy { it.key }
            .take(7) // Берем последние 7 дней
            .associate { entry ->
                entry.key.format(formatter) to entry.value
            }
        
        StatisticsUiState(
            totalSessions = totalSessions,
            totalWorkSessions = totalWorkSessions,
            totalWorkMinutes = totalWorkMinutes,
            completionRate = completionRate,
            dailyAverage = dailyAverage,
            workSessionsCount = workSessions.size,
            shortBreakSessionsCount = shortBreakSessions.size,
            longBreakSessionsCount = longBreakSessions.size,
            workSessionsPercentage = workSessionsPercentage,
            shortBreakSessionsPercentage = shortBreakSessionsPercentage,
            longBreakSessionsPercentage = longBreakSessionsPercentage,
            dailyStats = formattedDailyStats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )
} 