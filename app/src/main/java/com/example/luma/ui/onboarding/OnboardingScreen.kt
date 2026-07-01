package com.example.luma.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.data.model.EnergyType
import kotlinx.coroutines.launch
import com.example.luma.ui.tasks.color
import com.example.luma.ui.tasks.label
import com.example.luma.ui.tasks.description

private const val PAGE_COUNT = 4

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.factory(context)
    )
    val completed by viewModel.onboardingCompleted.collectAsState()

    LaunchedEffect(completed) {
        if (completed) onFinished()
    }

    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == PAGE_COUNT - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // Skip-knapp, döljs på sista sidan eftersom "Get started" tar samma roll
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (!isLastPage) {
                TextButton(onClick = { viewModel.completeOnboarding() }) {
                    Text(
                        text = "Skip",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> CheckInConceptPage()
                2 -> EnergyTypesPage()
                3 -> ReadyPage()
            }
        }

        PagerDots(
            pageCount = PAGE_COUNT,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
        )

        Button(
            onClick = {
                if (isLastPage) {
                    viewModel.completeOnboarding()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isLastPage) "Get started" else "Next",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun PagerDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                animationSpec = tween(durationMillis = 250),
                label = "dotWidth"
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(8.dp)
                    .width(width)
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun OnboardingPageScaffold(
    title: String,
    body: String,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(28.dp))
        content()
    }
}

@Composable
private fun WelcomePage() {
    OnboardingPageScaffold(
        title = "Welcome to Luma",
        body = "Luma isn't a traditional to-do list. It's a way to plan around your " +
                "energy, not against it, so your days feel sustainable instead of draining."
    )
}

@Composable
private fun CheckInConceptPage() {
    OnboardingPageScaffold(
        title = "Start with a check-in",
        body = "A quick daily check-in helps Luma understand how you're doing: " +
                "your energy, stress, focus, social battery, and sleep. No tracking " +
                "for the sake of tracking, just a moment to notice how you feel."
    )
}

@Composable
private fun EnergyTypesPage() {
    OnboardingPageScaffold(
        title = "Tasks have energy too",
        body = "Every task is tagged with the kind of energy it asks of you. " +
                "That makes it easier to match what you do with how you feel."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            EnergyType.entries.forEach { type ->
                EnergyTypeRow(type)
            }
        }
    }
}

@Composable
private fun EnergyTypeRow(type: EnergyType) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(
                    color = type.color(),
                    shape = CircleShape
                )
        )
        Column {
            Text(
                text = type.label(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = type.description(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReadyPage() {
    OnboardingPageScaffold(
        title = "You're all set",
        body = "Check in when you can, add tasks as they come up, and let Luma " +
                "gently point you toward what fits how you're feeling today."
    )
}