package com.mastermind.myownpomadoro.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.mastermind.myownpomadoro.MainActivity
import com.mastermind.myownpomadoro.R
import com.mastermind.myownpomadoro.domain.model.TimerState
import com.mastermind.myownpomadoro.service.PomodoroTimerService

/**
 * Виджет для отображения состояния таймера Pomodoro на домашнем экране.
 * Показывает текущее состояние таймера, оставшееся время и кнопки управления.
 */
class PomodoroWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Обновляем каждый экземпляр виджета
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        // Обрабатываем действия для кнопок виджета
        when (intent.action) {
            ACTION_START_TIMER -> {
                val serviceIntent = Intent(context, PomodoroTimerService::class.java).apply {
                    action = PomodoroTimerService.ACTION_START
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
            ACTION_PAUSE_TIMER -> {
                val serviceIntent = Intent(context, PomodoroTimerService::class.java).apply {
                    action = PomodoroTimerService.ACTION_PAUSE
                }
                context.startService(serviceIntent)
            }
            ACTION_RESET_TIMER -> {
                val serviceIntent = Intent(context, PomodoroTimerService::class.java).apply {
                    action = PomodoroTimerService.ACTION_RESET
                }
                context.startService(serviceIntent)
            }
        }
    }

    companion object {
        const val ACTION_START_TIMER = "com.mastermind.myownpomadoro.START_TIMER"
        const val ACTION_PAUSE_TIMER = "com.mastermind.myownpomadoro.PAUSE_TIMER"
        const val ACTION_RESET_TIMER = "com.mastermind.myownpomadoro.RESET_TIMER"
        
        /**
         * Обновляет состояние виджета
         */
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_pomodoro)
            
            // Создаем PendingIntent для открытия приложения при нажатии на виджет
            val pendingIntent = PendingIntent.getActivity(
                context, 0, 
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            
            // Получаем информацию о текущем состоянии таймера из SharedPreferences
            val prefs = context.getSharedPreferences("pomodoro_prefs", Context.MODE_PRIVATE)
            val isRunning = prefs.getBoolean("is_timer_running", false)
            val timeLeftMillis = prefs.getLong("time_left_millis", 0)
            val periodName = prefs.getString("current_period", context.getString(R.string.work_period))
            
            // Устанавливаем информацию в виджет
            views.setTextViewText(R.id.widget_period_name, periodName)
            views.setTextViewText(R.id.widget_time_left, formatTime(timeLeftMillis))
            
            // Настраиваем кнопки управления
            val startPendingIntent = PendingIntent.getBroadcast(
                context, 0,
                Intent(context, PomodoroWidget::class.java).setAction(ACTION_START_TIMER),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val pausePendingIntent = PendingIntent.getBroadcast(
                context, 1,
                Intent(context, PomodoroWidget::class.java).setAction(ACTION_PAUSE_TIMER),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val resetPendingIntent = PendingIntent.getBroadcast(
                context, 2,
                Intent(context, PomodoroWidget::class.java).setAction(ACTION_RESET_TIMER),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            views.setOnClickPendingIntent(R.id.widget_btn_start, startPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_btn_pause, pausePendingIntent)
            views.setOnClickPendingIntent(R.id.widget_btn_reset, resetPendingIntent)
            
            // Скрываем/показываем кнопки в зависимости от состояния
            views.setViewVisibility(R.id.widget_btn_start, if (isRunning) android.view.View.GONE else android.view.View.VISIBLE)
            views.setViewVisibility(R.id.widget_btn_pause, if (isRunning) android.view.View.VISIBLE else android.view.View.GONE)
            
            // Обновляем виджет
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        
        /**
         * Форматирует время в миллисекундах в формат MM:SS
         */
        private fun formatTime(millis: Long): String {
            val minutes = (millis / 1000) / 60
            val seconds = (millis / 1000) % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
} 