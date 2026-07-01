package com.example.luma.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.luma.data.model.CheckIn
import com.example.luma.data.model.Task

@Database(entities = [CheckIn::class, Task::class], version = 2)
abstract class LumaDatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: LumaDatabase? = null

        fun getDatabase(context: Context): LumaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LumaDatabase::class.java,
                    "luma_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}