package com.example.luma.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.LumaApplication
import com.example.luma.data.model.CheckIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CheckInViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as LumaApplication).repository

    private val _energy = MutableStateFlow(3)
    val energy: StateFlow<Int> = _energy

    private val _stress = MutableStateFlow(3)
    val stress: StateFlow<Int> = _stress

    private val _focus = MutableStateFlow(3)
    val focus: StateFlow<Int> = _focus

    private val _socialBattery = MutableStateFlow(3)
    val socialBattery: StateFlow<Int> = _socialBattery

    private val _sleepQuality = MutableStateFlow(3)
    val sleepQuality: StateFlow<Int> = _sleepQuality

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    // Ladda senaste värden när ViewModel skapas
    init {
        viewModelScope.launch {
            val today = repository.getTodaysCheckIn().first()
            // Om det redan finns en check-in idag, pre-fyll med de värdena
            // Om inte (ny dag), börja från default (3)
            today?.let { previous ->
                _energy.value = previous.energy
                _stress.value = previous.stress
                _focus.value = previous.focus
                _socialBattery.value = previous.socialBattery
                _sleepQuality.value = previous.sleepQuality
            }
        }
    }

    fun setEnergy(value: Int) { _energy.value = value }
    fun setStress(value: Int) { _stress.value = value }
    fun setFocus(value: Int) { _focus.value = value }
    fun setSocialBattery(value: Int) { _socialBattery.value = value }
    fun setSleepQuality(value: Int) { _sleepQuality.value = value }

    fun saveCheckIn() {
        viewModelScope.launch {
            repository.insertCheckIn(
                CheckIn(
                    energy = _energy.value,
                    stress = _stress.value,
                    focus = _focus.value,
                    socialBattery = _socialBattery.value,
                    sleepQuality = _sleepQuality.value
                )
            )
            _saved.value = true
        }
    }
}