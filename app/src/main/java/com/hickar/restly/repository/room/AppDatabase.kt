package com.hickar.restly.repository.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hickar.restly.repository.dao.CollectionDao
import com.hickar.restly.repository.dao.RequestDao
import com.hickar.restly.repository.dao.RequestGroupDao
import com.hickar.restly.repository.models.CollectionDTO
import com.hickar.restly.repository.models.RequestDTO
import com.hickar.restly.repository.models.RequestDirectoryDTO

@Database(
    entities = [
        RequestDTO::class,
        CollectionDTO::class,
        RequestDirectoryDTO::class
    ], version = 10
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun requestDao(database: AppDatabase = this): RequestDao
    abstract fun collectionDao(database: AppDatabase = this): CollectionDao
    abstract fun requestGroupDao(database: AppDatabase = this): RequestGroupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "restly"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}