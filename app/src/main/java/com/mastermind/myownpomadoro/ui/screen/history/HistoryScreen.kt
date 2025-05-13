package com.mastermind.myownpomadoro.ui.screen.history

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mastermind.myownpomadoro.R
import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.PomodoroSession
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Обрабатываем все события UI, включая новое событие CalendarIntent
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateTo -> { /* Навигация */ }
                is UiEvent.ShowSnackbar -> { /* Показ сообщения */ }
                is UiEvent.CalendarIntent -> {
                    // Запускаем Intent для добавления события в календарь
                    context.startActivity(event.intent)
                }
            }
        }
    }
    
    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Заголовок
                Text(
                    text = stringResource(R.string.history),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Кнопка экспорта
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { viewModel.exportSessions() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_export),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.export_data),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Список сессий
                if (uiState.sessions.isEmpty()) {
                    // Пустой список
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = null,
                            modifier = Modifier.height(64.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = stringResource(R.string.no_sessions_yet),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(R.string.complete_pomodoro_to_see_history),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    // Список сессий сгруппированный по датам
                    SessionsList(
                        sessions = uiState.sessions,
                        onExportSession = { viewModel.exportSession(it) },
                        onAddToCalendar = { viewModel.addSessionToCalendar(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun SessionItem(
    session: PomodoroSession,
    onExportClick: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (session.periodType) {
                PeriodType.WORK -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                PeriodType.SHORT_BREAK -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                PeriodType.LONG_BREAK -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
            }
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (session.periodType) {
                        PeriodType.WORK -> stringResource(R.string.work_period)
                        PeriodType.SHORT_BREAK -> stringResource(R.string.short_break)
                        PeriodType.LONG_BREAK -> stringResource(R.string.long_break)
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = stringResource(
                        R.string.session_time_range,
                        session.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        session.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${session.durationMinutes} мин",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (session.wasCompleted) {
                        stringResource(R.string.completed)
                    } else {
                        stringResource(R.string.interrupted)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (session.wasCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
            
            // Добавляем кнопку добавления в календарь
            IconButton(
                onClick = onCalendarClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Добавить в календарь",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // Существующая кнопка экспорта
            IconButton(
                onClick = onExportClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_export),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun SessionsList(
    sessions: List<PomodoroSession>,
    onExportSession: (PomodoroSession) -> Unit,
    onAddToCalendar: (PomodoroSession) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        val groupedSessions = sessions.groupBy { it.date }
        
        groupedSessions.forEach { (date, sessionsInGroup) ->
            item {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(sessionsInGroup) { session ->
                SessionItem(
                    session = session,
                    onExportClick = { onExportSession(session) },
                    onCalendarClick = { onAddToCalendar(session) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
} 