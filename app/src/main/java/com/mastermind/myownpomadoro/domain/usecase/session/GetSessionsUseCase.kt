package com.mastermind.myownpomadoro.domain.usecase.session

import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import com.mastermind.myownpomadoro.domain.repository.PomodoroSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения всех сессий
 */
class GetSessionsUseCase @Inject constructor(
    private val sessionRepository: PomodoroSessionRepository
) {
    operator fun invoke(): Flow<List<PomodoroSession>> {
        return sessionRepository.getSessions()
    }
} 