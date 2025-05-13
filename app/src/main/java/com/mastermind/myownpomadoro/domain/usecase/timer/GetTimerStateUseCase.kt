package com.mastermind.myownpomadoro.domain.usecase.timer

import com.mastermind.myownpomadoro.domain.model.PomodoroState
import com.mastermind.myownpomadoro.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения состояния таймера
 */
class GetTimerStateUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    operator fun invoke(): Flow<PomodoroState> {
        return timerRepository.getTimerState()
    }
} 