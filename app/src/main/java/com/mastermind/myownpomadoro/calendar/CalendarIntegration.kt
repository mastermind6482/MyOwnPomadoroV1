package com.mastermind.myownpomadoro.calendar

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CalendarContract
import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import android.Manifest
import android.net.Uri
import java.time.ZoneId
import java.util.TimeZone

/**
 * Класс для интеграции с календарем устройства.
 * Позволяет добавлять сессии Pomodoro в календарь пользователя.
 */
class CalendarIntegration(private val context: Context) {

    /**
     * Проверяет наличие разрешения на доступ к календарю
     */
    fun hasCalendarPermission(): Boolean {
        return context.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
               context.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Создает событие в календаре на основе сессии Pomodoro
     * через Intent (не требуется разрешение WRITE_CALENDAR)
     */
    fun addSessionToCalendarWithIntent(session: PomodoroSession): Intent {
        // Создаем Intent для добавления события в календарь
        val startTime = session.startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endTime = session.endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            putExtra(CalendarContract.Events.TITLE, getSessionTitle(session))
            putExtra(CalendarContract.Events.DESCRIPTION, getSessionDescription(session))
            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            putExtra(CalendarContract.Events.ALL_DAY, false)
        }
    }
    
    /**
     * Добавляет сессию Pomodoro напрямую в календарь
     * (требуется разрешение WRITE_CALENDAR)
     */
    fun addSessionToCalendar(session: PomodoroSession): Uri? {
        if (!hasCalendarPermission()) {
            return null
        }
        
        val startTime = session.startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endTime = session.endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        val contentResolver: ContentResolver = context.contentResolver
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, getDefaultCalendarId())
            put(CalendarContract.Events.TITLE, getSessionTitle(session))
            put(CalendarContract.Events.DESCRIPTION, getSessionDescription(session))
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(CalendarContract.Events.ALL_DAY, 0) // 0 для событий с определенным временем
            put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        }
        
        return contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }
    
    /**
     * Получает ID календаря по умолчанию
     */
    private fun getDefaultCalendarId(): Long {
        var calendarId: Long = -1
        
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )
        
        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            CalendarContract.Calendars.VISIBLE + " = 1",
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idCol = cursor.getColumnIndex(CalendarContract.Calendars._ID)
                calendarId = cursor.getLong(idCol)
            }
        }
        
        return calendarId
    }
    
    /**
     * Формирует заголовок события на основе типа сессии
     */
    private fun getSessionTitle(session: PomodoroSession): String {
        return when (session.periodType) {
            com.mastermind.myownpomadoro.domain.model.PeriodType.WORK -> "Pomodoro: Рабочая сессия"
            com.mastermind.myownpomadoro.domain.model.PeriodType.SHORT_BREAK -> "Pomodoro: Короткий перерыв"
            com.mastermind.myownpomadoro.domain.model.PeriodType.LONG_BREAK -> "Pomodoro: Длинный перерыв"
        }
    }
    
    /**
     * Формирует описание события с дополнительной информацией о сессии
     */
    private fun getSessionDescription(session: PomodoroSession): String {
        val durationMinutes = session.durationMinutes
        val status = if (session.wasCompleted) "Завершена" else "Прервана"
        
        return "Сессия Pomodoro\n" +
               "Продолжительность: $durationMinutes минут\n" +
               "Статус: $status"
    }
} 