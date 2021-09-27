package com.hickar.restly.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hickar.restly.models.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Request::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun requestDao(): RequestDao

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
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance

                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.requestDao())
                    }
                }
            }

            suspend fun populateDatabase(requestDao: RequestDao) {
                requestDao.deleteAll()

                requestDao.insert(Request(1, "GET", "New Request 1", "https://hickar.space/api"))
                requestDao.insert(Request(2, "POST", "New Request 2", "https://hickar.space/api"))
                requestDao.insert(Request(3, "PUT", "New Request 3", "https://hickar.space/api"))
                requestDao.insert(Request(4, "DELETE", "New Request 4", "https://hickar.space/api"))
                requestDao.insert(Request(5, "GET", "New Request 5", "https://hickar.space/api"))
            }
        }
    }
}