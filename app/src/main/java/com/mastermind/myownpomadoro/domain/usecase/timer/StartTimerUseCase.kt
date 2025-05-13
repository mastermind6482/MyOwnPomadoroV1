package com.mastermind.myownpomadoro.domain.usecase.timer

import com.mastermind.myownpomadoro.domain.repository.TimerRepository
import javax.inject.Inject

/**
 * Use Case для запуска таймера
 */
class StartTimerUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke() {
        timerRepository.startTimer()
    }
} 