package com.mastermind.myownpomadoro.domain.usecase.session

import com.mastermind.myownpomadoro.domain.repository.PomodoroSessionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use Case для получения ежедневной статистики
 */
class GetDailyStatsUseCase @Inject constructor(
    private val sessionRepository: PomodoroSessionRepository
) {
    operator fun invoke(): Flow<Map<LocalDate, Int>> {
        return sessionRepository.getDailyStats()
    }
} 