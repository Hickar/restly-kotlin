package com.hickar.restly.repository.dao

import android.database.sqlite.SQLiteException
import androidx.room.*
import com.hickar.restly.repository.models.RequestDTO
import java.lang.Exception

@Dao
interface RequestDao {
    @Throws(SQLiteException::class)
    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getById(id: Long): RequestDTO

    @Query("SELECT * FROM requests")
    suspend fun getAll(): MutableList<RequestDTO>

    @Throws(SQLiteException::class)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: RequestDTO): Long

    @Update
    suspend fun update(request: RequestDTO)

    @Delete
    suspend fun delete(request: RequestDTO)

    @Delete
    suspend fun deleteAll(vararg requests: RequestDTO)
}