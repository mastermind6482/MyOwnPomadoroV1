package com.mastermind.myownpomadoro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val dateMillis: Long,
    val durationMinutes: Int,
    val periodType: String,
    val wasCompleted: Boolean
) {
    companion object {
        fun fromDomain(domainModel: PomodoroSession): PomodoroSessionEntity {
            return PomodoroSessionEntity(
                id = domainModel.id,
                startTimeMillis = domainModel.startTime.toEpochSecond(java.time.ZoneOffset.UTC) * 1000,
                endTimeMillis = domainModel.endTime.toEpochSecond(java.time.ZoneOffset.UTC) * 1000,
                dateMillis = domainModel.date.toEpochDay() * 24 * 60 * 60 * 1000,
                durationMinutes = domainModel.durationMinutes,
                periodType = domainModel.periodType.name,
                wasCompleted = domainModel.wasCompleted
            )
        }
    }

    fun toDomain(): PomodoroSession {
        return PomodoroSession(
            id = id,
            startTime = LocalDateTime.ofEpochSecond(startTimeMillis / 1000, 0, java.time.ZoneOffset.UTC),
            endTime = LocalDateTime.ofEpochSecond(endTimeMillis / 1000, 0, java.time.ZoneOffset.UTC),
            date = LocalDate.ofEpochDay(dateMillis / (24 * 60 * 60 * 1000)),
            durationMinutes = durationMinutes,
            periodType = PeriodType.valueOf(periodType),
            wasCompleted = wasCompleted
        )
    }
} 