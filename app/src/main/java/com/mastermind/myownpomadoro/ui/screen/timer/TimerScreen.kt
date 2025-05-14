package com.mastermind.myownpomadoro.ui.screen.timer

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getPeriodTypeText(uiState.currentPeriodType),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = getColorForPeriodType(uiState.currentPeriodType)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val completedCount = uiState.completedWorkPeriods
                        
                        for (i in 0 until 4) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i < completedCount % 4) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                            ) {}
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = stringResource(
                                R.string.completed_pomodoros, 
                                uiState.completedWorkPeriods
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                val progress by animateFloatAsState(
                    targetValue = if (uiState.totalTime.seconds > 0) {
                        uiState.remainingTime.seconds.toFloat() / uiState.totalTime.seconds.toFloat()
                    } else {
                        0f
                    },
                    animationSpec = tween(300),
                    label = "progress"
                )
                
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = getColorForPeriodType(uiState.currentPeriodType).copy(alpha = 0.2f)
                )
                
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = getColorForPeriodType(uiState.currentPeriodType)
                )
                
                Card(
                    modifier = Modifier
                        .size(220.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val minutes = uiState.remainingTime.toMinutes()
                            val seconds = uiState.remainingTime.minusMinutes(minutes).seconds
                            
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                color = getColorForPeriodType(uiState.currentPeriodType)
                            )
                            
                            Text(
                                text = getTimerStateText(uiState.timerState),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_reset),
                        contentDescription = stringResource(R.string.reset),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Button(
                    onClick = {
                        if (uiState.timerState == TimerState.RUNNING) {
                            viewModel.pauseTimer()
                        } else {
                            viewModel.startTimer()
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getColorForPeriodType(uiState.currentPeriodType)
                    )
                ) {
                    Icon(
                        painter = if (uiState.timerState == TimerState.RUNNING) {
                            painterResource(id = R.drawable.ic_pause)
                        } else {
                            painterResource(id = R.drawable.ic_play)
                        },
                        contentDescription = if (uiState.timerState == TimerState.RUNNING) {
                            stringResource(R.string.pause)
                        } else {
                            stringResource(R.string.start)
                        },
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                FilledTonalButton(
                    onClick = { viewModel.skipToNextPeriod() },
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_skip),
                        contentDescription = stringResource(R.string.skip),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = getColorForPeriodType(uiState.currentPeriodType).copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = getPeriodDescription(uiState.currentPeriodType),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
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