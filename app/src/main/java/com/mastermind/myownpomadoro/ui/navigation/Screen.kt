package com.mastermind.myownpomadoro.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mastermind.myownpomadoro.R

sealed class Screen(
    val route: String,
    @StringRes val titleResId: Int,
    @DrawableRes val iconResId: Int
) {
    data object Timer : Screen(
        route = "timer",
        titleResId = R.string.timer,
        iconResId = R.drawable.ic_timer
    )
    
    data object Statistics : Screen(
        route = "statistics",
        titleResId = R.string.statistics,
        iconResId = R.drawable.ic_statistics
    )
    
    data object History : Screen(
        route = "history",
        titleResId = R.string.history,
        iconResId = R.drawable.ic_history
    )
    
    data object Settings : Screen(
        route = "settings",
        titleResId = R.string.settings,
        iconResId = R.drawable.ic_settings
    )
} 