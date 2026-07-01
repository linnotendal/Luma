package com.example.luma.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.data.model.CheckIn

@Composable
fun HomeScreen(
    onStartCheckIn: () -> Unit,
    onGoToTasks: () -> Unit,
    onShowOnboarding: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val todaysCheckIn by viewModel.todaysCheckIn.collectAsState()
    var showOnboardingDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Luma",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Plan with your energy, not against it.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (todaysCheckIn != null) {
                CheckInSummaryCard(
                    checkIn = todaysCheckIn!!,
                    suggestion = viewModel.getSuggestion(todaysCheckIn!!)
                )
            } else {
                NoCheckInCard(onStartCheckIn = onStartCheckIn)
            }

            Button(
                onClick = onStartCheckIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (todaysCheckIn != null) "Update check-in" else "Start check-in",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        IconButton(
            onClick = { showOnboardingDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(36.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = "Show onboarding again",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    if (showOnboardingDialog) {
        AlertDialog(
            onDismissRequest = { showOnboardingDialog = false },
            title = { Text("Show onboarding again?") },
            text = { Text("You'll see the welcome screens again, starting from the beginning.") },
            confirmButton = {
                TextButton(onClick = {
                    showOnboardingDialog = false
                    onShowOnboarding()
                }) {
                    Text("Show it")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOnboardingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CheckInSummaryCard(checkIn: CheckIn, suggestion: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Today's check-in",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EnergyDot(label = "Energy", value = checkIn.energy)
            EnergyDot(label = "Stress", value = checkIn.stress)
            EnergyDot(label = "Focus", value = checkIn.focus)
            EnergyDot(label = "Social", value = checkIn.socialBattery)
            EnergyDot(label = "Sleep", value = checkIn.sleepQuality)
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        Text(
            text = suggestion,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EnergyDot(label: String, value: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun NoCheckInCard(onStartCheckIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "No check-in yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Take a moment to reflect on how you're feeling. It only takes a minute.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}