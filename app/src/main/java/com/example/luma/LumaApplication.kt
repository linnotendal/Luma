package com.example.luma

import android.app.Application
import com.example.luma.data.SeedData
import com.example.luma.data.database.LumaDatabase
import com.example.luma.data.repository.LumaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LumaApplication : Application() {
    val database by lazy { LumaDatabase.getDatabase(this) }
    val repository by lazy {
        LumaRepository(
            database.checkInDao(),
            database.taskDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        seedIfNeeded()
    }

    private fun seedIfNeeded() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingCheckIns = database.checkInDao().getCheckInCount()
                if (existingCheckIns == 0) {
                    SeedData.generateCheckIns().forEach {
                        database.checkInDao().insert(it)
                    }
                    SeedData.generateTasks().forEach {
                        database.taskDao().insert(it)
                    }
                }
            } catch (e: Exception) {
                // Seeding failed silently
            }
        }
    }
}