package com.example.luma.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.LumaApplication
import com.example.luma.data.model.CheckIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as LumaApplication).repository

    // Reaktiv: uppdateras automatiskt när en check-in sparas
    val todaysCheckIn: StateFlow<CheckIn?> = repository.getTodaysCheckIn()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun getSuggestion(checkIn: CheckIn): String {
        return when {
            checkIn.energy <= 2 && checkIn.stress >= 4 ->
                "You seem low on energy and stressed, try to focus on small, easy tasks today."
            checkIn.energy <= 2 ->
                "Your energy is low today, be kind to yourself and prioritize rest."
            checkIn.stress >= 4 ->
                "Stress is high today, consider breaking tasks into smaller steps."
            checkIn.focus >= 4 && checkIn.energy >= 4 ->
                "You seem sharp and energized, this could be a great day for focused work."
            checkIn.sleepQuality <= 2 ->
                "Poor sleep can affect everything, take it easy and don't overcommit today."
            else ->
                "You're doing okay, take it one step at a time."
        }
    }
}