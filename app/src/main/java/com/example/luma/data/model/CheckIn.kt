package com.example.luma.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_ins")
data class CheckIn(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long = System.currentTimeMillis(), // sparas som tidsstämpel
    val energy: Int,        // 1–5
    val stress: Int,        // 1–5
    val focus: Int,         // 1–5
    val socialBattery: Int, // 1–5
    val sleepQuality: Int,  // 1–5
    val note: String = ""   // valfri fritextkommentar
)