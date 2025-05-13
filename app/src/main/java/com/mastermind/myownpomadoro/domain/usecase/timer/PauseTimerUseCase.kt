package com.mastermind.myownpomadoro.domain.usecase.timer

import com.mastermind.myownpomadoro.domain.repository.TimerRepository
import javax.inject.Inject

/**
 * Use Case для приостановки таймера
 */
class PauseTimerUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke() {
        timerRepository.pauseTimer()
    }
} 