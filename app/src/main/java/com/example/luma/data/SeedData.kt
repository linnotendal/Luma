package com.example.luma.data

import com.example.luma.data.model.CheckIn
import com.example.luma.data.model.EnergyType
import com.example.luma.data.model.Priority
import com.example.luma.data.model.Task
import java.util.Calendar

object SeedData {

    fun generateCheckIns(): List<CheckIn> {
        val calendar = Calendar.getInstance()
        val checkIns = mutableListOf<CheckIn>()

        // Mönster: tröttare i början av veckan, bättre mot fredag
        // Sömn varierar, påverkar fokus nästa dag
        val dayPatterns = listOf(
            // energy, stress, focus, social, sleep
            intArrayOf(2, 4, 2, 2, 2), // måndag – tung start
            intArrayOf(3, 3, 3, 3, 3), // tisdag
            intArrayOf(3, 3, 4, 3, 4), // onsdag
            intArrayOf(4, 2, 4, 4, 4), // torsdag – flödar
            intArrayOf(4, 2, 3, 4, 3), // fredag
            intArrayOf(3, 1, 2, 5, 4), // lördag – social
            intArrayOf(2, 1, 2, 3, 5)  // söndag – återhämtning
        )

        for (daysAgo in 30 downTo 1) {
            // Hoppa över ~20% av dagarna för realism
            if (daysAgo % 5 == 0) continue

            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, (0..45).random())

            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val pattern = dayPatterns[(dayOfWeek - 1) % 7]

            // Lägg till lite slumpmässig variation (±1)
            fun vary(base: Int) = (base + (-1..1).random()).coerceIn(1, 5)

            checkIns.add(
                CheckIn(
                    date = calendar.timeInMillis,
                    energy = vary(pattern[0]),
                    stress = vary(pattern[1]),
                    focus = vary(pattern[2]),
                    socialBattery = vary(pattern[3]),
                    sleepQuality = vary(pattern[4])
                )
            )
        }
        return checkIns
    }

    fun generateTasks(): List<Task> {
        val calendar = Calendar.getInstance()

        fun daysAgo(n: Int): Long {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -n)
            return calendar.timeInMillis
        }

        return listOf(
            Task(title = "Morning walk", energyType = EnergyType.MOVEMENT,
                priority = Priority.LOW, isCompleted = true, createdAt = daysAgo(20)),
            Task(title = "Write project proposal", energyType = EnergyType.FOCUS,
                priority = Priority.HIGH, isCompleted = true, createdAt = daysAgo(18)),
            Task(title = "Call mom", energyType = EnergyType.PRESENCE,
                priority = Priority.MEDIUM, isCompleted = true, createdAt = daysAgo(15)),
            Task(title = "Read for 30 minutes", energyType = EnergyType.REST,
                priority = Priority.LOW, isCompleted = true, createdAt = daysAgo(10)),
            Task(title = "Gym session", energyType = EnergyType.MOVEMENT,
                priority = Priority.MEDIUM, isCompleted = true, createdAt = daysAgo(8)),
            Task(title = "Review pull requests", energyType = EnergyType.FOCUS,
                priority = Priority.HIGH, isCompleted = true, createdAt = daysAgo(6)),
            Task(title = "Catch up with a friend", energyType = EnergyType.PRESENCE,
                priority = Priority.MEDIUM, isCompleted = false, createdAt = daysAgo(2)),
            Task(title = "Deep work session", energyType = EnergyType.FOCUS,
                priority = Priority.HIGH, isCompleted = false, createdAt = daysAgo(1)),
            Task(title = "Evening stretch", energyType = EnergyType.MOVEMENT,
                priority = Priority.LOW, isCompleted = false, createdAt = daysAgo(1)),
        )
    }
}