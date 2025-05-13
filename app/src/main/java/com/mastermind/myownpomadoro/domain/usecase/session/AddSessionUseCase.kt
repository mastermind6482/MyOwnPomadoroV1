package com.mastermind.myownpomadoro.domain.usecase.session

import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import com.mastermind.myownpomadoro.domain.repository.PomodoroSessionRepository
import javax.inject.Inject

/**
 * Use Case для добавления новой сессии
 */
class AddSessionUseCase @Inject constructor(
    private val sessionRepository: PomodoroSessionRepository
) {
    suspend operator fun invoke(session: PomodoroSession): Long {
        return sessionRepository.addSession(session)
    }
} 