package com.mastermind.myownpomadoro.di

import android.content.Context
import androidx.room.Room
import com.mastermind.myownpomadoro.data.local.PomodoroDatabase
import com.mastermind.myownpomadoro.data.local.dao.PomodoroSessionDao
import com.mastermind.myownpomadoro.data.repository.PomodoroSessionRepositoryImpl
import com.mastermind.myownpomadoro.data.repository.SettingsRepositoryImpl
import com.mastermind.myownpomadoro.data.repository.TimerRepositoryImpl
import com.mastermind.myownpomadoro.domain.repository.PomodoroSessionRepository
import com.mastermind.myownpomadoro.domain.repository.SettingsRepository
import com.mastermind.myownpomadoro.domain.repository.TimerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PomodoroDatabase {
        return Room.databaseBuilder(
            context,
            PomodoroDatabase::class.java,
            PomodoroDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun providePomodoroSessionDao(database: PomodoroDatabase): PomodoroSessionDao {
        return database.pomodoroSessionDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindPomodoroSessionRepository(
        pomodoroSessionRepositoryImpl: PomodoroSessionRepositoryImpl
    ): PomodoroSessionRepository
    
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
    
    @Binds
    @Singleton
    abstract fun bindTimerRepository(
        timerRepositoryImpl: TimerRepositoryImpl
    ): TimerRepository
} 