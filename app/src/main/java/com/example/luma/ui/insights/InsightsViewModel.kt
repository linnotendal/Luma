package com.example.luma.ui.insights

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.LumaApplication
import com.example.luma.data.model.CheckIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class InsightCard(
    val title: String,
    val body: String
)

data class InsightsUiState(
    val recentCheckIns: List<CheckIn> = emptyList(),
    val insights: List<InsightCard> = emptyList(),
    val hasEnoughData: Boolean = false
)

class InsightsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as LumaApplication).repository

    val uiState: StateFlow<InsightsUiState> = repository.getAllCheckIns()
        .map { allCheckIns ->
            if (allCheckIns.isEmpty()) return@map InsightsUiState()

            val recent = allCheckIns.take(14) // Senaste 14 dagarna för grafen
            val hasEnough = allCheckIns.size >= 5

            InsightsUiState(
                recentCheckIns = recent,
                insights = if (hasEnough) generateInsights(allCheckIns) else emptyList(),
                hasEnoughData = hasEnough
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InsightsUiState()
        )

    private fun generateInsights(checkIns: List<CheckIn>): List<InsightCard> {
        val insights = mutableListOf<InsightCard>()

        // Samband: sömn → fokus
        val goodSleep = checkIns.filter { it.sleepQuality >= 4 }
        val poorSleep = checkIns.filter { it.sleepQuality <= 2 }
        if (goodSleep.size >= 3 && poorSleep.size >= 2) {
            val focusGoodSleep = goodSleep.map { it.focus }.average()
            val focusPoorSleep = poorSleep.map { it.focus }.average()
            if (focusGoodSleep - focusPoorSleep >= 0.8) {
                insights.add(InsightCard(
                    title = "Sleep shapes your focus",
                    body = "On days after good sleep your focus averages ${
                        "%.1f".format(focusGoodSleep)
                    }, compared to ${
                        "%.1f".format(focusPoorSleep)
                    } after poor sleep."
                ))
            }
        }

        // Bästa dagen i veckan (energi)
        val byWeekday = checkIns.groupBy { dayOfWeek(it.date) }
        val bestDay = byWeekday
            .mapValues { (_, ins) -> ins.map { it.energy }.average() }
            .maxByOrNull { it.value }
        if (bestDay != null) {
            insights.add(InsightCard(
                title = "Your strongest day",
                body = "${bestDay.key}s tend to be your highest-energy day. Consider saving your most demanding tasks for then."
            ))
        }

        // Stress-trend
        val recentStress = checkIns.take(7).map { it.stress }.average()
        val olderStress = checkIns.drop(7).take(7).map { it.stress }.average()
        if (checkIns.size >= 14) {
            when {
                recentStress - olderStress >= 0.8 -> insights.add(InsightCard(
                    title = "Stress is rising",
                    body = "Your stress level has been higher this week than the week before. Try to protect some time for rest."
                ))
                olderStress - recentStress >= 0.8 -> insights.add(InsightCard(
                    title = "Stress is easing",
                    body = "You seem to be handling things better lately. Your stress has come down compared to last week."
                ))
            }
        }

        // Energi vs socialt batteri
        val lowEnergyHighSocial = checkIns.count { it.energy <= 2 && it.socialBattery >= 4 }
        if (lowEnergyHighSocial >= 3) {
            insights.add(InsightCard(
                title = "Social energy is resilient",
                body = "Even on low-energy days, your social battery tends to stay charged. Social time might actually help you recover."
            ))
        }

        // Konsistens
        val streak = calculateStreak(checkIns)
        if (streak >= 3) {
            insights.add(InsightCard(
                title = "Consistent check-ins",
                body = "You've checked in $streak days in a row. That kind of self-awareness adds up."
            ))
        }

        return insights
    }

    private fun calculateStreak(checkIns: List<CheckIn>): Int {
        if (checkIns.isEmpty()) return 0
        val cal = Calendar.getInstance()
        var streak = 0
        var expectedDay = today()

        for (checkIn in checkIns) {
            val checkInDay = startOfDay(checkIn.date)
            if (checkInDay == expectedDay) {
                streak++
                cal.timeInMillis = expectedDay
                cal.add(Calendar.DAY_OF_YEAR, -1)
                expectedDay = cal.timeInMillis
            } else if (checkInDay < expectedDay) {
                break
            }
        }
        return streak
    }

    private fun today(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun startOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun dayOfWeek(timestamp: Long): String {
        return when (Calendar.getInstance().apply { timeInMillis = timestamp }
            .get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Sunday"
        }
    }
}