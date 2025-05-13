package com.mastermind.myownpomadoro.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mastermind.myownpomadoro.R
import com.mastermind.myownpomadoro.ui.screen.settings.components.SettingsDurationItem
import com.mastermind.myownpomadoro.ui.screen.settings.components.SettingsSwitchItem

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
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
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Настройки таймера
                Text(
                    text = stringResource(R.string.timer_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SettingsDurationItem(
                    title = stringResource(R.string.work_duration),
                    value = uiState.workDurationMinutes,
                    onValueChange = { viewModel.updateWorkDuration(it) },
                    minValue = 1,
                    maxValue = 60
                )
                
                SettingsDurationItem(
                    title = stringResource(R.string.short_break_duration),
                    value = uiState.shortBreakDurationMinutes,
                    onValueChange = { viewModel.updateShortBreakDuration(it) },
                    minValue = 1,
                    maxValue = 30
                )
                
                SettingsDurationItem(
                    title = stringResource(R.string.long_break_duration),
                    value = uiState.longBreakDurationMinutes,
                    onValueChange = { viewModel.updateLongBreakDuration(it) },
                    minValue = 1,
                    maxValue = 60
                )
                
                SettingsDurationItem(
                    title = stringResource(R.string.pomodoros_until_long_break),
                    value = uiState.periodsUntilLongBreak,
                    onValueChange = { viewModel.updatePeriodsUntilLongBreak(it) },
                    minValue = 1,
                    maxValue = 10
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Настройки автоматизации
                Text(
                    text = stringResource(R.string.automation_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SettingsSwitchItem(
                    title = stringResource(R.string.auto_start_breaks),
                    description = stringResource(R.string.auto_start_breaks_description),
                    checked = uiState.autoStartBreaks,
                    onCheckedChange = { viewModel.updateAutoStartBreaks(it) }
                )
                
                SettingsSwitchItem(
                    title = stringResource(R.string.auto_start_pomodoros),
                    description = stringResource(R.string.auto_start_pomodoros_description),
                    checked = uiState.autoStartPomodoros,
                    onCheckedChange = { viewModel.updateAutoStartPomodoros(it) }
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Настройки уведомлений
                Text(
                    text = stringResource(R.string.notification_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SettingsSwitchItem(
                    title = stringResource(R.string.sound_enabled),
                    description = stringResource(R.string.sound_enabled_description),
                    checked = uiState.soundEnabled,
                    onCheckedChange = { viewModel.updateSoundEnabled(it) }
                )
                
                SettingsSwitchItem(
                    title = stringResource(R.string.vibration_enabled),
                    description = stringResource(R.string.vibration_enabled_description),
                    checked = uiState.vibrationEnabled,
                    onCheckedChange = { viewModel.updateVibrationEnabled(it) }
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Настройки интерфейса
                Text(
                    text = stringResource(R.string.interface_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SettingsSwitchItem(
                    title = stringResource(R.string.use_system_theme),
                    description = stringResource(R.string.use_system_theme_description),
                    checked = uiState.useSystemTheme,
                    onCheckedChange = { viewModel.updateUseSystemTheme(it) }
                )
                
                // Показываем переключатель темной темы только если не используется системная тема
                if (!uiState.useSystemTheme) {
                    SettingsSwitchItem(
                        title = stringResource(R.string.dark_theme),
                        description = stringResource(R.string.dark_theme_description),
                        checked = uiState.isDarkThemeEnabled,
                        onCheckedChange = { viewModel.updateDarkTheme(it) }
                    )
                }
                
                SettingsSwitchItem(
                    title = stringResource(R.string.keep_screen_on),
                    description = stringResource(R.string.keep_screen_on_description),
                    checked = uiState.keepScreenOn,
                    onCheckedChange = { viewModel.updateKeepScreenOn(it) }
                )
                
                // Добавляем пространство внизу для удобства скролла
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
} 