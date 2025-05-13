package com.mastermind.myownpomadoro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mastermind.myownpomadoro.data.local.entity.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroSessionDao {
    
    @Query("SELECT * FROM pomodoro_sessions ORDER BY dateMillis DESC, startTimeMillis DESC")
    fun getAllSessions(): Flow<List<PomodoroSessionEntity>>
    
    @Query("SELECT * FROM pomodoro_sessions WHERE dateMillis = :dateMillis ORDER BY startTimeMillis DESC")
    fun getSessionsByDate(dateMillis: Long): Flow<List<PomodoroSessionEntity>>
    
    @Query("SELECT * FROM pomodoro_sessions WHERE dateMillis BETWEEN :startDateMillis AND :endDateMillis ORDER BY dateMillis DESC, startTimeMillis DESC")
    fun getSessionsByDateRange(startDateMillis: Long, endDateMillis: Long): Flow<List<PomodoroSessionEntity>>
    
    @Query("SELECT * FROM pomodoro_sessions WHERE periodType = :periodType ORDER BY dateMillis DESC, startTimeMillis DESC")
    fun getSessionsByType(periodType: String): Flow<List<PomodoroSessionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSessionEntity): Long
    
    @Delete
    suspend fun deleteSession(session: PomodoroSessionEntity)
    
    @Query("SELECT * FROM pomodoro_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): PomodoroSessionEntity?
    
    @Query("SELECT dateMillis, COUNT(*) as count FROM pomodoro_sessions WHERE periodType = 'WORK' AND wasCompleted = 1 GROUP BY dateMillis ORDER BY dateMillis DESC")
    fun getDailyCompletedWorkSessions(): Flow<Map<@MapColumn(columnName = "dateMillis") Long, @MapColumn(columnName = "count") Int>>
} 