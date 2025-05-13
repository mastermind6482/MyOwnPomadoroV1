package com.mastermind.myownpomadoro.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.mastermind.myownpomadoro.MainActivity
import com.mastermind.myownpomadoro.R
import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import com.mastermind.myownpomadoro.domain.model.PomodoroState
import com.mastermind.myownpomadoro.domain.model.TimerState
import com.mastermind.myownpomadoro.domain.repository.SettingsRepository
import com.mastermind.myownpomadoro.domain.repository.TimerRepository
import com.mastermind.myownpomadoro.domain.usecase.session.AddSessionUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroTimerService : Service() {
    
    @Inject
    lateinit var timerRepository: TimerRepository
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    @Inject
    lateinit var addSessionUseCase: AddSessionUseCase
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null
    
    private var currentState: PomodoroState? = null
    private var startTime: LocalDateTime? = null
    
    companion object {
        const val ACTION_START = "com.mastermind.myownpomadoro.START_TIMER"
        const val ACTION_PAUSE = "com.mastermind.myownpomadoro.PAUSE_TIMER"
        const val ACTION_RESET = "com.mastermind.myownpomadoro.RESET_TIMER"
        const val ACTION_SKIP = "com.mastermind.myownpomadoro.SKIP_TIMER"
        
        const val NOTIFICATION_CHANNEL_ID = "pomodoro_timer_channel"
        const val NOTIFICATION_ID = 1
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        // Получаем WakeLock для предотвращения перехода устройства в спящий режим
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "PomodoroTimer::WakeLock"
        )
        
        // Наблюдаем за состоянием таймера
        serviceScope.launch {
            timerRepository.getTimerState().collect { state ->
                currentState = state
                updateNotification(state)
                
                // Если таймер завершен, проигрываем звук и вибрацию
                if (state.timerState == TimerState.COMPLETED) {
                    onTimerCompleted()
                }
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                serviceScope.launch {
                    startTime = LocalDateTime.now()
                    timerRepository.startTimer()
                    startTimer()
                }
            }
            ACTION_PAUSE -> {
                serviceScope.launch {
                    timerRepository.pauseTimer()
                    timerJob?.cancel()
                }
            }
            ACTION_RESET -> {
                serviceScope.launch {
                    timerRepository.resetTimer()
                    timerJob?.cancel()
                }
            }
            ACTION_SKIP -> {
                serviceScope.launch {
                    currentState?.let {
                        // Сохраняем текущую сессию как незавершенную
                        saveSession(false)
                        
                        // Переходим к следующему периоду
                        timerRepository.moveToNextPeriod()
                    }
                }
            }
        }
        
        // Запускаем сервис в foreground режиме
        startForeground(NOTIFICATION_ID, createNotification(currentState ?: PomodoroState()))
        
        return START_STICKY
    }
    
    private fun startTimer() {
        // Отменяем предыдущий таймер, если он был
        timerJob?.cancel()
        
        // Получаем WakeLock, если это настроено
        serviceScope.launch {
            val settings = settingsRepository.getSettings().first()
            if (settings.keepScreenOn && !wakeLock!!.isHeld) {
                wakeLock?.acquire(10*60*1000L) // 10 минут максимум
            }
        }
        
        // Запускаем новый таймер
        timerJob = serviceScope.launch {
            while (true) {
                delay(1000) // Обновляем каждую секунду
                
                val state = timerRepository.getTimerState().first()
                if (state.timerState == TimerState.RUNNING) {
                    val newRemainingTime = state.remainingTime.minusSeconds(1)
                    timerRepository.updateRemainingTime(newRemainingTime)
                }
            }
        }
    }
    
    private fun onTimerCompleted() {
        serviceScope.launch {
            // Сохраняем завершенную сессию
            saveSession(true)
            
            // Проигрываем звук и вибрацию
            val settings = settingsRepository.getSettings().first()
            
            if (settings.soundEnabled) {
                playSound(settings.soundUri)
            }
            
            if (settings.vibrationEnabled) {
                vibrate()
            }
            
            // Увеличиваем счетчик завершенных периодов
            timerRepository.incrementCompletedPeriods()
            
            // Переходим к следующему периоду
            timerRepository.moveToNextPeriod()
        }
    }
    
    private suspend fun saveSession(wasCompleted: Boolean) {
        currentState?.let { state ->
            startTime?.let { start ->
                val now = LocalDateTime.now()
                val session = PomodoroSession(
                    startTime = start,
                    endTime = now,
                    date = LocalDate.now(),
                    durationMinutes = state.totalTime.toMinutes().toInt(),
                    periodType = state.currentPeriodType,
                    wasCompleted = wasCompleted
                )
                addSessionUseCase(session)
                
                // Сбрасываем время начала
                startTime = null
            }
        }
    }
    
    private fun playSound(soundUri: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                
                // Пробуем воспроизвести пользовательский звук
                try {
                    setDataSource(applicationContext, Uri.parse(soundUri))
                    prepare()
                } catch (e: Exception) {
                    // Если не удалось загрузить пользовательский звук, используем системный
                    e.printStackTrace()
                    
                    // Выбираем системный звук уведомления по умолчанию
                    val defaultSoundUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
                    setDataSource(applicationContext, defaultSoundUri)
                    prepare()
                }
                
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 250, 500), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 500, 250, 500), -1)
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(state: PomodoroState): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        // Действия для уведомления
        val startPauseAction = if (state.timerState == TimerState.RUNNING) {
            NotificationCompat.Action(
                R.drawable.ic_pause,
                getString(R.string.pause),
                PendingIntent.getService(
                    this,
                    1,
                    Intent(this, PomodoroTimerService::class.java).setAction(ACTION_PAUSE),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play,
                getString(R.string.start),
                PendingIntent.getService(
                    this,
                    2,
                    Intent(this, PomodoroTimerService::class.java).setAction(ACTION_START),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
        
        val resetAction = NotificationCompat.Action(
            R.drawable.ic_reset,
            getString(R.string.reset),
            PendingIntent.getService(
                this,
                3,
                Intent(this, PomodoroTimerService::class.java).setAction(ACTION_RESET),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        
        val skipAction = NotificationCompat.Action(
            R.drawable.ic_skip,
            getString(R.string.skip),
            PendingIntent.getService(
                this,
                4,
                Intent(this, PomodoroTimerService::class.java).setAction(ACTION_SKIP),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        
        // Текст для уведомления
        val contentTitle = when (state.currentPeriodType) {
            PeriodType.WORK -> getString(R.string.work_period)
            PeriodType.SHORT_BREAK -> getString(R.string.short_break)
            PeriodType.LONG_BREAK -> getString(R.string.long_break)
        }
        
        val minutes = state.remainingTime.toMinutes()
        val seconds = state.remainingTime.minusMinutes(minutes).seconds
        val contentText = String.format("%02d:%02d", minutes, seconds)
        
        // Создаем уведомление
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .addAction(startPauseAction)
            .addAction(resetAction)
            .addAction(skipAction)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }
    
    private fun updateNotification(state: PomodoroState) {
        val notification = createNotification(state)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
    }
} 