package com.hickar.restly.repository.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hickar.restly.repository.dao.RequestDao
import com.hickar.restly.repository.models.RequestDTO
import kotlinx.coroutines.CoroutineScope

@Database(entities = [RequestDTO::class], version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract fun requestDao(database: AppDatabase = this): RequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "restly")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}