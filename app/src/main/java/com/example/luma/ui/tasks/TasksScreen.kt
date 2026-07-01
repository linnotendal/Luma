package com.example.luma.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.data.model.EnergyType
import com.example.luma.data.model.Priority
import com.example.luma.data.model.Task
import com.example.luma.ui.theme.*

@Composable
fun TasksScreen(
    onAddTask: () -> Unit,
    onEditTask: (Task) -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()

    // Håller koll på vilken flik som är aktiv
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 48.dp)
            )

            // Flikar för Active / Completed
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Completed") }
                )
            }

            val currentList = if (selectedTab == 0) tasks else completedTasks

            if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "No active tasks" else "No completed tasks",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(currentList) { task ->
                        TaskCard(
                            task = task,
                            isCompleted = selectedTab == 1,
                            onComplete = { viewModel.completeTask(task) },
                            onRestore = { viewModel.restoreTask(task) },
                            onTap = { onEditTask(task) }
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            FloatingActionButton(
                onClick = onAddTask,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    isCompleted: Boolean,
    onComplete: () -> Unit,
    onRestore: () -> Unit,
    onTap: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onTap() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = task.energyType.color(),
                        shape = RoundedCornerShape(6.dp)
                    )
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${task.energyType.label()} · ${task.priority.label()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Visar återställ-knapp för completed, klar-knapp för active
        IconButton(onClick = if (isCompleted) onRestore else onComplete) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.Refresh else Icons.Default.Check,
                contentDescription = if (isCompleted) "Restore task" else "Complete task",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Hjälpfunktioner för att visa rätt färg och text för varje energityp
fun EnergyType.color(): Color = when (this) {
    EnergyType.REST -> ColorRest
    EnergyType.FOCUS -> ColorFocus
    EnergyType.PRESENCE -> ColorPresence
    EnergyType.MOVEMENT -> ColorMovement
}

fun EnergyType.label(): String = when (this) {
    EnergyType.REST -> "Rest"
    EnergyType.FOCUS -> "Focus"
    EnergyType.PRESENCE -> "Presence"
    EnergyType.MOVEMENT -> "Movement"
}

fun EnergyType.description(): String = when (this) {
    EnergyType.REST -> "Low effort, no focus needed"
    EnergyType.FOCUS -> "Needs concentration and quiet"
    EnergyType.PRESENCE -> "Social or emotional energy"
    EnergyType.MOVEMENT -> "Physical activity"
}

fun Priority.label(): String = when (this) {
    Priority.LOW -> "Low"
    Priority.MEDIUM -> "Medium"
    Priority.HIGH -> "High"
}