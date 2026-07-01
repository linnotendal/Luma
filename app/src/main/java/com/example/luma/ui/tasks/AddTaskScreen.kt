package com.example.luma.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.data.model.EnergyType
import com.example.luma.data.model.Priority
import com.example.luma.data.model.Task

@Composable
fun AddTaskScreen(
    onTaskSaved: () -> Unit,
    onBack: () -> Unit,
    existingTask: Task? = null,
    viewModel: TaskViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val energyType by viewModel.energyType.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val taskSaved by viewModel.taskSaved.collectAsState()

    // Om vi redigerar en befintlig task, ladda in dess värden
    LaunchedEffect(existingTask) {
        if (existingTask != null) {
            viewModel.loadTaskForEditing(existingTask)
        } else {
            viewModel.resetForm()
        }
    }

    LaunchedEffect(taskSaved) {
        if (taskSaved) {
            viewModel.resetForm()
            onTaskSaved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = if (existingTask != null) "Edit task" else "New task",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.setTitle(it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.setDescription(it) },
            label = { Text("Description (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        // Energityp-väljare med beskrivning för varje typ
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Energy type",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EnergyType.entries.forEach { type ->
                    val selected = energyType == type
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (selected) type.color().copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = if (selected) 2.dp else 0.dp,
                                color = if (selected) type.color()
                                else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setEnergyType(type) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    color = type.color(),
                                    shape = RoundedCornerShape(7.dp)
                                )
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Priority",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { viewModel.setPriority(p) },
                        label = { Text(p.label()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Ta bort-knapp visas bara om vi redigerar en befintlig task
        if (existingTask != null) {
            OutlinedButton(
                onClick = {
                    viewModel.deleteTask(existingTask)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete task", style = MaterialTheme.typography.titleMedium)
            }
        }

        Button(
            onClick = { viewModel.saveTask() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = title.isNotBlank()
        ) {
            Text(
                text = if (existingTask != null) "Save changes" else "Save task",
                style = MaterialTheme.typography.titleMedium
            )
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}