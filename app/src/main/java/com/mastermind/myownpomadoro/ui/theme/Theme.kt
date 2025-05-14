package com.mastermind.myownpomadoro.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.mastermind.myownpomadoro.data.local.preferences.UserPreferences

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryLight,
    secondary = SecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = SecondaryLight.copy(alpha = 0.1f),
    onSecondaryContainer = SecondaryLight,
    tertiary = TertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryLight.copy(alpha = 0.1f),
    onTertiaryContainer = TertiaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceLight.copy(alpha = 0.7f),
    error = Color(0xFFB00020)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.White,
    primaryContainer = PrimaryDark.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryDark,
    secondary = SecondaryDark,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryDark.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryDark,
    tertiary = TertiaryDark,
    onTertiary = Color.Black,
    tertiaryContainer = TertiaryDark.copy(alpha = 0.2f),
    onTertiaryContainer = TertiaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceDark.copy(alpha = 0.7f),
    error = Color(0xFFCF6679)
)

@Composable
fun MyOwnPomadoroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Принимаем настройки темы напрямую
    forceDarkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Определяем, нужно ли использовать темную тему
    val useDarkTheme = forceDarkTheme || darkTheme
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color to transparent for edge-to-edge design
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}