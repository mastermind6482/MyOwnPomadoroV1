package com.mastermind.myownpomadoro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mastermind.myownpomadoro.data.local.dao.PomodoroSessionDao
import com.mastermind.myownpomadoro.data.local.entity.PomodoroSessionEntity
import com.mastermind.myownpomadoro.data.local.typeconverter.DateTimeConverters

@Database(
    entities = [PomodoroSessionEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class PomodoroDatabase : RoomDatabase() {
    abstract fun pomodoroSessionDao(): PomodoroSessionDao

    companion object {
        const val DATABASE_NAME = "pomodoro_db"
    }
} 