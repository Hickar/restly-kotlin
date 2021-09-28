package com.hickar.restly.repository.dao

import androidx.room.*
import com.hickar.restly.repository.models.Request
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getById(id: Int): Request

    @Query("SELECT * FROM requests")
    suspend fun getAll(): Flow<List<Request>>

    @Insert
    suspend fun insert(request: Request): Long

    @Update
    suspend fun update(request: Request)

    @Delete
    suspend fun delete(request: Request)

    @Delete
    suspend fun deleteAll(vararg requests: Request)
}