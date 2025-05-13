package com.mastermind.myownpomadoro.ui.screen.timer

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mastermind.myownpomadoro.R
import com.mastermind.myownpomadoro.domain.model.PeriodType
import com.mastermind.myownpomadoro.domain.model.TimerState
import com.mastermind.myownpomadoro.service.PomodoroTimerService

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Запускаем сервис при изменении состояния таймера
    LaunchedEffect(uiState.timerState) {
        val intent = Intent(context, PomodoroTimerService::class.java)
        
        when (uiState.timerState) {
            TimerState.RUNNING -> {
                intent.action = PomodoroTimerService.ACTION_START
                context.startService(intent)
            }
            TimerState.PAUSED -> {
                intent.action = PomodoroTimerService.ACTION_PAUSE
                context.startService(intent)
            }
            TimerState.IDLE -> {
                intent.action = PomodoroTimerService.ACTION_RESET
                context.startService(intent)
            }
            TimerState.COMPLETED -> {
                // Обработка завершения таймера происходит в сервисе
            }
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок текущего периода
            Text(
                text = getPeriodTypeText(uiState.currentPeriodType),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Счетчик завершенных помидоров
            Text(
                text = stringResource(
                    R.string.completed_pomodoros, 
                    uiState.completedWorkPeriods
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Таймер с круговым прогресс-баром
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Анимированный прогресс
                val progress by animateFloatAsState(
                    targetValue = if (uiState.totalTime.seconds > 0) {
                        uiState.remainingTime.seconds.toFloat() / uiState.totalTime.seconds.toFloat()
                    } else {
                        0f
                    },
                    label = "progress"
                )
                
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = getColorForPeriodType(uiState.currentPeriodType)
                )
                
                // Оставшееся время
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val minutes = uiState.remainingTime.toMinutes()
                    val seconds = uiState.remainingTime.minusMinutes(minutes).seconds
                    
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Text(
                        text = getTimerStateText(uiState.timerState),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.alpha(0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Кнопки управления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Кнопка старт/пауза
                Button(
                    onClick = {
                        if (uiState.timerState == TimerState.RUNNING) {
                            viewModel.pauseTimer()
                        } else {
                            viewModel.startTimer()
                        }
                    },
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (uiState.timerState == TimerState.RUNNING) {
                                R.drawable.ic_pause
                            } else {
                                R.drawable.ic_play
                            }
                        ),
                        contentDescription = if (uiState.timerState == TimerState.RUNNING) {
                            stringResource(R.string.pause)
                        } else {
                            stringResource(R.string.start)
                        }
                    )
                }
                
                // Кнопка сброса
                OutlinedButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_reset),
                        contentDescription = stringResource(R.string.reset)
                    )
                }
                
                // Кнопка пропуска
                FilledTonalButton(
                    onClick = { viewModel.skipToNextPeriod() },
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_skip),
                        contentDescription = stringResource(R.string.skip)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Описание текущего режима
            Text(
                text = getPeriodDescription(uiState.currentPeriodType),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
private fun getPeriodTypeText(periodType: PeriodType): String {
    return when (periodType) {
        PeriodType.WORK -> stringResource(R.string.work_period)
        PeriodType.SHORT_BREAK -> stringResource(R.string.short_break)
        PeriodType.LONG_BREAK -> stringResource(R.string.long_break)
    }
}

@Composable
private fun getPeriodDescription(periodType: PeriodType): String {
    return when (periodType) {
        PeriodType.WORK -> stringResource(R.string.work_period_description)
        PeriodType.SHORT_BREAK -> stringResource(R.string.short_break_description)
        PeriodType.LONG_BREAK -> stringResource(R.string.long_break_description)
    }
}

@Composable
private fun getTimerStateText(timerState: TimerState): String {
    return when (timerState) {
        TimerState.RUNNING -> stringResource(R.string.running)
        TimerState.PAUSED -> stringResource(R.string.paused)
        TimerState.IDLE -> stringResource(R.string.ready)
        TimerState.COMPLETED -> stringResource(R.string.completed)
    }
}

@Composable
private fun getColorForPeriodType(periodType: PeriodType) = when (periodType) {
    PeriodType.WORK -> MaterialTheme.colorScheme.primary
    PeriodType.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
    PeriodType.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
} 