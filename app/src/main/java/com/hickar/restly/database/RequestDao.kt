package com.hickar.restly.database

import androidx.room.*
import com.hickar.restly.models.Request
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests WHERE id = :id")
    fun getById(id: Int): Request

    @Query("SELECT * FROM requests")
    fun getAll(): Flow<List<Request>>

    @Insert
    suspend fun insert(request: Request): Long

    @Update
    fun update(request: Request)

    @Delete
    fun delete(request: Request)

    @Delete
    fun deleteAll(vararg requests: Request)
}