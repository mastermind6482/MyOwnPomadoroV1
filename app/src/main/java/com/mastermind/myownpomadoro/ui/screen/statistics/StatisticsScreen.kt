package com.mastermind.myownpomadoro.ui.screen.statistics

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mastermind.myownpomadoro.R
import com.mastermind.myownpomadoro.domain.model.PeriodType
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
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
                    .verticalScroll(rememberScrollState())
            ) {
                // Заголовок
                Text(
                    text = stringResource(R.string.statistics),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Карточки со статистикой
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = stringResource(R.string.total_pomodoros),
                        value = uiState.totalWorkSessions.toString(),
                        icon = R.drawable.ic_timer,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    StatCard(
                        title = stringResource(R.string.total_time),
                        value = "${uiState.totalWorkMinutes} мин",
                        icon = R.drawable.ic_time,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = stringResource(R.string.completion_rate),
                        value = "${uiState.completionRate}%",
                        icon = R.drawable.ic_check,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    
                    StatCard(
                        title = stringResource(R.string.daily_average),
                        value = uiState.dailyAverage.toString(),
                        icon = R.drawable.ic_calendar,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // График активности
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.activity_chart),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (uiState.dailyStats.isEmpty()) {
                            // Пустой график
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_data_available),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        } else {
                            // Отображаем график
                            ActivityChart(
                                dailyStats = uiState.dailyStats,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Круговая диаграмма распределения типов сессий
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.session_distribution),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (uiState.totalSessions == 0) {
                            // Пустая диаграмма
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_data_available),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        } else {
                            // Отображаем диаграмму
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Круговая диаграмма
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PieChart(
                                        data = mapOf(
                                            PeriodType.WORK to uiState.workSessionsCount,
                                            PeriodType.SHORT_BREAK to uiState.shortBreakSessionsCount,
                                            PeriodType.LONG_BREAK to uiState.longBreakSessionsCount
                                        ),
                                        modifier = Modifier.size(180.dp)
                                    )
                                }
                                
                                // Легенда
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    PieChartLegendItem(
                                        color = MaterialTheme.colorScheme.primary,
                                        label = stringResource(R.string.work_periods),
                                        value = "${uiState.workSessionsCount} (${uiState.workSessionsPercentage}%)"
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    PieChartLegendItem(
                                        color = MaterialTheme.colorScheme.secondary,
                                        label = stringResource(R.string.short_breaks),
                                        value = "${uiState.shortBreakSessionsCount} (${uiState.shortBreakSessionsPercentage}%)"
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    PieChartLegendItem(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        label = stringResource(R.string.long_breaks),
                                        value = "${uiState.longBreakSessionsCount} (${uiState.longBreakSessionsPercentage}%)"
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Добавляем пространство внизу для удобства скролла
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    ElevatedCard(
        modifier = modifier,
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
            // Иконка в цветном круге
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Значение
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Заголовок
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ActivityChart(
    dailyStats: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val maxValue = dailyStats.values.maxOrNull() ?: 1
    val gridLineColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    val barColor = MaterialTheme.colorScheme.primary
    val dateTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Отрисовка графика
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val barWidth = width / dailyStats.size.coerceAtLeast(1)
                
                // Отрисовка горизонтальных линий сетки
                val gridLines = 5
                
                for (i in 0..gridLines) {
                    val y = height - (height * i / gridLines)
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }
                
                // Отрисовка столбцов
                dailyStats.entries.forEachIndexed { index, entry ->
                    val barHeight = (entry.value.toFloat() / maxValue) * height
                    val barX = index * barWidth
                    
                    drawRect(
                        color = barColor,
                        topLeft = Offset(barX + barWidth * 0.2f, height - barHeight),
                        size = Size(barWidth * 0.6f, barHeight)
                    )
                }
            }
            
            // Отрисовка подписей дат по оси X
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dailyStats.keys.firstOrNull()?.let { firstDate ->
                        Text(
                            text = firstDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = dateTextColor
                        )
                    }
                    
                    dailyStats.keys.lastOrNull()?.let { lastDate ->
                        Text(
                            text = lastDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = dateTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(
    data: Map<PeriodType, Int>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum().coerceAtLeast(1)
    val workColor = MaterialTheme.colorScheme.primary
    val shortBreakColor = MaterialTheme.colorScheme.secondary
    val longBreakColor = MaterialTheme.colorScheme.tertiary
    val backgroundColor = MaterialTheme.colorScheme.background
    
    val colorMap = mapOf(
        PeriodType.WORK to workColor,
        PeriodType.SHORT_BREAK to shortBreakColor,
        PeriodType.LONG_BREAK to longBreakColor
    )
    
    Canvas(modifier = modifier) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        var startAngle = -90f
        
        data.forEach { (type, count) ->
            val sweepAngle = 360f * count / total
            
            drawArc(
                color = colorMap[type] ?: Color.Gray,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            
            startAngle += sweepAngle
        }
        
        // Рисуем белый круг в центре для создания эффекта кольца
        drawCircle(
            color = backgroundColor,
            radius = radius * 0.6f,
            center = center
        )
        
        // Рисуем обводку
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}

@Composable
fun PieChartLegendItem(
    color: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
} 