package com.example.luma.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val energyType: EnergyType,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class EnergyType {
    REST,
    FOCUS,
    PRESENCE,
    MOVEMENT,
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}