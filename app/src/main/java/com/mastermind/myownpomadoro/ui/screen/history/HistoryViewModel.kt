package com.mastermind.myownpomadoro.ui.screen.history

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import com.mastermind.myownpomadoro.domain.repository.PomodoroSessionRepository
import com.mastermind.myownpomadoro.domain.usecase.session.GetSessionsUseCase
import com.mastermind.myownpomadoro.calendar.CalendarIntegration
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HistoryUiState(
    val sessions: List<PomodoroSession> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class NavigateTo(val route: String) : UiEvent()
    data class CalendarIntent(val intent: Intent) : UiEvent()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getSessionsUseCase: GetSessionsUseCase,
    private val sessionRepository: PomodoroSessionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _uiEvent = kotlinx.coroutines.flow.MutableSharedFlow<UiEvent>()
    
    val uiEvent = _uiEvent
    
    val uiState: StateFlow<HistoryUiState> = combine(
        getSessionsUseCase(),
        _isLoading,
        _error
    ) { sessions, isLoading, error ->
        HistoryUiState(
            sessions = sessions,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )
    
    fun exportSessions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val jsonData = sessionRepository.exportSessions()
                
                // Создаем временный файл
                val fileName = "pomodoro_sessions_${LocalDate.now().format(DateTimeFormatter.ISO_DATE)}.json"
                val file = File(context.cacheDir, fileName)
                
                // Записываем данные в файл
                FileOutputStream(file).use { it.write(jsonData.toByteArray()) }
                
                // Создаем URI для файла с использованием FileProvider
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                // Создаем Intent для отправки файла
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_SUBJECT, "Pomodoro Sessions Export")
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                // Запускаем Intent с выбором приложения
                val chooserIntent = Intent.createChooser(shareIntent, "Export Sessions")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * Добавляет выбранную сессию в календарь
     */
    fun addSessionToCalendar(session: PomodoroSession) {
        val calendarIntegration = CalendarIntegration(context)
        
        // Создаем Intent для взаимодействия с календарем
        val intent = calendarIntegration.addSessionToCalendarWithIntent(session)
        
        // Intent нужно запустить из Activity или Fragment
        // Поэтому возвращаем его через Flow для обработки в UI
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.CalendarIntent(intent))
        }
    }

    /**
     * Экспортирует отдельную сессию
     */
    fun exportSession(session: PomodoroSession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val jsonData = sessionRepository.exportSession(session.id)
                
                // Создаем временный файл
                val fileName = "pomodoro_session_${session.id}_${LocalDate.now().format(DateTimeFormatter.ISO_DATE)}.json"
                val file = File(context.cacheDir, fileName)
                
                // Записываем данные в файл
                FileOutputStream(file).use { it.write(jsonData.toByteArray()) }
                
                // Создаем URI для файла с использованием FileProvider
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                // Создаем Intent для отправки файла
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_SUBJECT, "Pomodoro Session Export")
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                // Запускаем Intent с выбором приложения
                val chooserIntent = Intent.createChooser(shareIntent, "Export Session")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
} 