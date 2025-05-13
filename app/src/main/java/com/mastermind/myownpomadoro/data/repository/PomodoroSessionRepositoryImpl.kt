package com.mastermind.myownpomadoro.data.repository

import com.mastermind.myownpomadoro.data.local.dao.PomodoroSessionDao
import com.mastermind.myownpomadoro.data.local.entity.PomodoroSessionEntity
import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import com.mastermind.myownpomadoro.domain.repository.PomodoroSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroSessionRepositoryImpl @Inject constructor(
    private val sessionDao: PomodoroSessionDao
) : PomodoroSessionRepository {

    override fun getSessions(): Flow<List<PomodoroSession>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSessionsByDate(date: LocalDate): Flow<List<PomodoroSession>> {
        val dateMillis = date.toEpochDay() * 24 * 60 * 60 * 1000
        return sessionDao.getSessionsByDate(dateMillis).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSessionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<PomodoroSession>> {
        val startDateMillis = startDate.toEpochDay() * 24 * 60 * 60 * 1000
        val endDateMillis = endDate.toEpochDay() * 24 * 60 * 60 * 1000
        return sessionDao.getSessionsByDateRange(startDateMillis, endDateMillis).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSessionsByType(periodType: PeriodType): Flow<List<PomodoroSession>> {
        return sessionDao.getSessionsByType(periodType.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addSession(session: PomodoroSession): Long {
        val entity = PomodoroSessionEntity.fromDomain(session)
        return sessionDao.insertSession(entity)
    }

    override suspend fun deleteSession(session: PomodoroSession) {
        val entity = PomodoroSessionEntity.fromDomain(session)
        sessionDao.deleteSession(entity)
    }

    override fun getDailyStats(): Flow<Map<LocalDate, Int>> {
        return sessionDao.getDailyCompletedWorkSessions().map { statsMap ->
            statsMap.mapKeys { entry ->
                LocalDate.ofEpochDay(entry.key / (24 * 60 * 60 * 1000))
            }
        }
    }

    override suspend fun exportSessions(): String {
        val sessions = sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
        
        // Создаем простую структуру для экспорта
        data class ExportSession(
            val date: String,
            val startTime: String,
            val endTime: String,
            val durationMinutes: Int,
            val type: String,
            val completed: Boolean
        )
        
        val exportData = sessions.map { sessionList ->
            sessionList.map { session ->
                ExportSession(
                    date = session.date.toString(),
                    startTime = session.startTime.toString(),
                    endTime = session.endTime.toString(),
                    durationMinutes = session.durationMinutes,
                    type = session.periodType.name,
                    completed = session.wasCompleted
                )
            }
        }
        
        // Возвращаем JSON строку
        return Json.encodeToString(exportData)
    }

    override suspend fun exportSession(sessionId: Long): String {
        // Получаем сессию по ID
        val session = sessionDao.getSessionById(sessionId)
            ?: throw IllegalArgumentException("Session with id $sessionId not found")
            
        // Сериализуем сессию в JSON используя анонимный класс
        val sessionDto = object {
            val id = session.id
            val date = LocalDate.ofEpochDay(session.dateMillis / (24 * 60 * 60 * 1000)).toString()
            val startTime = LocalDateTime.ofEpochSecond(session.startTimeMillis / 1000, 0, ZoneOffset.UTC).toString()
            val endTime = LocalDateTime.ofEpochSecond(session.endTimeMillis / 1000, 0, ZoneOffset.UTC).toString()
            val durationMinutes = session.durationMinutes
            val type = session.periodType
            val completed = session.wasCompleted
        }
        
        // Возвращаем JSON строку
        return Json.encodeToString(sessionDto)
    }
} 