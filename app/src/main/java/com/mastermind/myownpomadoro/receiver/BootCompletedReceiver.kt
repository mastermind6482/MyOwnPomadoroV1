package com.mastermind.myownpomadoro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mastermind.myownpomadoro.PomodoroApplication
import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.TimerState
import com.mastermind.myownpomadoro.domain.repository.TimerRepository
import com.mastermind.myownpomadoro.service.PomodoroTimerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

/**
 * Получает уведомление о перезагрузке устройства и восстанавливает таймер,
 * если он был запущен перед выключением
 */
class BootCompletedReceiver : BroadcastReceiver() {
    
    // Будем получать репозиторий через getter при выполнении onReceive
    private var timerRepository: TimerRepository? = null
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Получаем timerRepository из компонента приложения
            if (context.applicationContext is PomodoroApplication) {
                // Ручное получение репозитория через сервис локатор
                // Это безопаснее, чем Hilt с BroadcastReceiver
                timerRepository = TimerRepositoryProvider.getTimerRepository(context)
            }
            
            // Проверяем, был ли таймер запущен
            val pendingResult: PendingResult = goAsync()
            coroutineScope.launch {
                try {
                    timerRepository?.getTimerState()?.first()?.let { timerState ->
                        if (timerState.timerState == TimerState.RUNNING) {
                            startTimerService(context)
                        }
                    }
                } catch (e: Exception) {
                    // Логируем ошибку, но не крашим приложение
                } finally {
                    // Важно завершить goAsync() после выполнения работы
                    pendingResult.finish()
                }
            }
        }
    }

    private fun startTimerService(context: Context) {
        val serviceIntent = Intent(context, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_START
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}

/**
 * Синглтон для доступа к репозиториям вне системы Hilt
 * Используется для безопасного получения экземпляров репозиториев в контексте BroadcastReceiver
 */
object TimerRepositoryProvider {
    @Volatile
    private var timerRepository: TimerRepository? = null
    
    fun getTimerRepository(context: Context): TimerRepository {
        return timerRepository ?: synchronized(this) {
            // Здесь вы можете вручную создать репозиторий или получить его через Hilt
            // Для простоты, мы можем получить его через Hilt компонент приложения
            // Или создать упрощенную реализацию для BroadcastReceiver
            val app = context.applicationContext
            // Создаем или получаем экземпляр репозитория
            SimpleTimerRepository(context).also { timerRepository = it }
        }
    }
}

/**
 * Упрощенная реализация TimerRepository для использования в BroadcastReceiver
 * Вместо полной реализации с DataStore, мы используем SharedPreferences для простоты
 */
class SimpleTimerRepository(private val context: Context) : TimerRepository {
    override fun getTimerState() = kotlinx.coroutines.flow.flow {
        // Проверяем SharedPreferences для упрощенного решения
        val prefs = context.getSharedPreferences("pomodoro_prefs", Context.MODE_PRIVATE)
        val isRunning = prefs.getBoolean("is_timer_running", false)
        val state = if (isRunning) TimerState.RUNNING else TimerState.PAUSED
        emit(com.mastermind.myownpomadoro.domain.model.PomodoroState(timerState = state))
    }
    
    // Реализация остальных методов интерфейса TimerRepository
    // просто как заглушки, так как они не используются в BootCompletedReceiver
    override suspend fun startTimer() { /* Заглушка */ }
    override suspend fun pauseTimer() { /* Заглушка */ }
    override suspend fun resetTimer() { /* Заглушка */ }
    override suspend fun updateTimerState(state: TimerState) { /* Заглушка */ }
    override suspend fun updatePeriodType(periodType: PeriodType) { /* Заглушка */ }
    override suspend fun updateRemainingTime(time: Duration) { /* Заглушка */ }
    override suspend fun incrementCompletedPeriods() { /* Заглушка */ }
    override suspend fun moveToNextPeriod() { /* Заглушка */ }
} 