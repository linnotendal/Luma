package com.example.luma.data.repository

import com.example.luma.data.database.CheckInDao
import com.example.luma.data.database.TaskDao
import com.example.luma.data.model.CheckIn
import com.example.luma.data.model.Task
import kotlinx.coroutines.flow.Flow

class LumaRepository(
    private val checkInDao: CheckInDao,
    private val taskDao: TaskDao
) {
    // Check-in
    suspend fun insertCheckIn(checkIn: CheckIn) = checkInDao.insert(checkIn)
    fun getAllCheckIns(): Flow<List<CheckIn>> = checkInDao.getAllCheckIns()
    suspend fun getLatestCheckIn(): CheckIn? = checkInDao.getLatestCheckIn()

    // Ny: returnerar Flow<CheckIn?> för dagens datum
    fun getTodaysCheckIn(): Flow<CheckIn?> {
        val startOfDay = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        return checkInDao.getTodaysCheckIn(startOfDay)
    }

    // Tasks
    suspend fun insertTask(task: Task) = taskDao.insert(task)
    suspend fun updateTask(task: Task) = taskDao.update(task)
    suspend fun deleteTask(task: Task) = taskDao.delete(task)
    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()
    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()
}