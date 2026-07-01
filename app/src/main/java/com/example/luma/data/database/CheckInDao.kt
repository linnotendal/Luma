package com.example.luma.data.database

import androidx.room.*
import com.example.luma.data.model.CheckIn
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Insert
    suspend fun insert(checkIn: CheckIn)

    @Query("SELECT * FROM check_ins ORDER BY date DESC")
    fun getAllCheckIns(): Flow<List<CheckIn>>

    @Query("SELECT * FROM check_ins ORDER BY date DESC LIMIT 1")
    suspend fun getLatestCheckIn(): CheckIn?

    // Ny: hämtar dagens check-in som en Flow (null om ingen gjorts idag)
    @Query("""
        SELECT * FROM check_ins 
        WHERE date >= :startOfDay 
        ORDER BY date DESC 
        LIMIT 1
    """)
    fun getTodaysCheckIn(startOfDay: Long): Flow<CheckIn?>

    @Query("SELECT COUNT(*) FROM check_ins")
    suspend fun getCheckInCount(): Int
}