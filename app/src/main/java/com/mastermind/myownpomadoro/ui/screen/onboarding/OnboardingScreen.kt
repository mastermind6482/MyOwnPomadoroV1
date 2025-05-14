package com.mastermind.myownpomadoro.ui.screen.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mastermind.myownpomadoro.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage(
            title = stringResource(R.string.onboarding_welcome_title),
            description = stringResource(R.string.onboarding_welcome_desc),
            imageResId = R.drawable.ic_timer,
            backgroundColor = MaterialTheme.colorScheme.primary
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_how_it_works_title),
            description = stringResource(R.string.onboarding_how_it_works_desc),
            imageResId = R.drawable.ic_work,
            backgroundColor = MaterialTheme.colorScheme.secondary
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_focus_title),
            description = stringResource(R.string.onboarding_focus_desc),
            imageResId = R.drawable.ic_focus,
            backgroundColor = MaterialTheme.colorScheme.tertiary
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_break_title),
            description = stringResource(R.string.onboarding_break_desc),
            imageResId = R.drawable.ic_break,
            backgroundColor = MaterialTheme.colorScheme.primary
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_track_title),
            description = stringResource(R.string.onboarding_track_desc),
            imageResId = R.drawable.ic_statistics,
            backgroundColor = MaterialTheme.colorScheme.secondary
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { position ->
            OnboardingPageScreen(page = pages[position])
        }

        // Индикаторы страниц и кнопки навигации
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.BottomCenter)
        ) {
            // Индикаторы страниц
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                pages.forEachIndexed { index, _ ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 12.dp else 10.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                    )
                }
            }

            // Кнопки навигации
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка пропустить
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.completeOnboarding()
                            onFinishOnboarding()
                        }
                    }) {
                        Text(
                            text = stringResource(R.string.onboarding_skip),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(64.dp))
                }

                // Кнопка далее/завершить
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                viewModel.completeOnboarding()
                                onFinishOnboarding()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage < pages.size - 1) 
                            stringResource(R.string.onboarding_next) 
                        else 
                            stringResource(R.string.onboarding_start),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    if (pagerState.currentPage < pages.size - 1) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(R.string.onboarding_next)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = stringResource(R.string.onboarding_start)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageScreen(page: OnboardingPage) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(page.backgroundColor.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Изображение
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(page.backgroundColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = page.imageResId),
                    contentDescription = page.title,
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Заголовок
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Описание
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageResId: Int,
    val backgroundColor: Color
)