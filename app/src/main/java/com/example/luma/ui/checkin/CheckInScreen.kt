package com.example.luma.ui.checkin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.theme.*

@Composable
fun CheckInScreen(
    onCheckInSaved: () -> Unit,
    viewModel: CheckInViewModel = viewModel()
) {
    val energy by viewModel.energy.collectAsState()
    val stress by viewModel.stress.collectAsState()
    val focus by viewModel.focus.collectAsState()
    val socialBattery by viewModel.socialBattery.collectAsState()
    val sleepQuality by viewModel.sleepQuality.collectAsState()
    val saved by viewModel.saved.collectAsState()

    // Visar bekräftelse-overlay i 1.5 sekunder innan vi navigerar vidare
    var showConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(saved) {
        if (saved) {
            showConfirmation = true
            kotlinx.coroutines.delay(1500)
            onCheckInSaved()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Check in",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "How are you feeling right now?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Varje slider får en färg kopplad till känslan
            EnergySlider(
                label = "Energy",
                endpoints = "Low" to "High",
                value = energy,
                activeColor = ColorRest,
                onValueChange = { viewModel.setEnergy(it) }
            )
            EnergySlider(
                label = "Stress",
                endpoints = "Calm" to "Overwhelmed",
                value = stress,
                activeColor = ColorMovement,
                onValueChange = { viewModel.setStress(it) }
            )
            EnergySlider(
                label = "Focus",
                endpoints = "Scattered" to "Sharp",
                value = focus,
                activeColor = ColorFocus,
                onValueChange = { viewModel.setFocus(it) }
            )
            EnergySlider(
                label = "Social battery",
                endpoints = "Drained" to "Charged",
                value = socialBattery,
                activeColor = ColorPresence,
                onValueChange = { viewModel.setSocialBattery(it) }
            )
            EnergySlider(
                label = "Sleep quality",
                endpoints = "Poor" to "Great",
                value = sleepQuality,
                activeColor = Dusk400,
                onValueChange = { viewModel.setSleepQuality(it) }
            )

            Button(
                onClick = { viewModel.saveCheckIn() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save check-in", style = MaterialTheme.typography.titleMedium)
            }
        }

        // Animerad bekräftelse-overlay som tonar in och ut
        AnimatedVisibility(
            visible = showConfirmation,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 40.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "Check-in saved",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Have a good day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun EnergySlider(
    label: String,
    endpoints: Pair<String, String>,
    value: Int,
    activeColor: Color,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = endpoints.first,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = endpoints.second,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End
            )
        }
    }
}