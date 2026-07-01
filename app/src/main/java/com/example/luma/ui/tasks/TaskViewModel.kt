package com.example.luma.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.LumaApplication
import com.example.luma.data.model.EnergyType
import com.example.luma.data.model.Priority
import com.example.luma.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as LumaApplication).repository

    val tasks: StateFlow<List<Task>> = repository.getActiveTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val completedTasks: StateFlow<List<Task>> = repository.getCompletedTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Formulär-state – används både för att skapa och redigera tasks
    private val _editingTask = MutableStateFlow<Task?>(null)
    val editingTask: StateFlow<Task?> = _editingTask

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _energyType = MutableStateFlow(EnergyType.FOCUS)
    val energyType: StateFlow<EnergyType> = _energyType

    private val _priority = MutableStateFlow(Priority.MEDIUM)
    val priority: StateFlow<Priority> = _priority

    private val _taskSaved = MutableStateFlow(false)
    val taskSaved: StateFlow<Boolean> = _taskSaved

    fun setTitle(value: String) { _title.value = value }
    fun setDescription(value: String) { _description.value = value }
    fun setEnergyType(value: EnergyType) { _energyType.value = value }
    fun setPriority(value: Priority) { _priority.value = value }

    // Laddar en befintlig task i formuläret för redigering
    fun loadTaskForEditing(task: Task) {
        _editingTask.value = task
        _title.value = task.title
        _description.value = task.description
        _energyType.value = task.energyType
        _priority.value = task.priority
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = true))
        }
    }

    // Återaktivera en completed task
    fun restoreTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = false))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Sparar antingen en ny task eller uppdaterar en befintlig
    fun saveTask() {
        if (_title.value.isBlank()) return
        viewModelScope.launch {
            val existing = _editingTask.value
            if (existing != null) {
                repository.updateTask(
                    existing.copy(
                        title = _title.value,
                        description = _description.value,
                        energyType = _energyType.value,
                        priority = _priority.value
                    )
                )
            } else {
                repository.insertTask(
                    Task(
                        title = _title.value,
                        description = _description.value,
                        energyType = _energyType.value,
                        priority = _priority.value
                    )
                )
            }
            _taskSaved.value = true
        }
    }

    fun resetForm() {
        _editingTask.value = null
        _title.value = ""
        _description.value = ""
        _energyType.value = EnergyType.FOCUS
        _priority.value = Priority.MEDIUM
        _taskSaved.value = false
    }
}