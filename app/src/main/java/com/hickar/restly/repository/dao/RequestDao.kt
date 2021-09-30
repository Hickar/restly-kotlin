package com.hickar.restly.repository.dao

import androidx.room.*
import com.hickar.restly.repository.models.RequestDTO

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getById(id: Long): RequestDTO

    @Query("SELECT * FROM requests")
    suspend fun getAll(): MutableList<RequestDTO>

    @Insert
    suspend fun insert(request: RequestDTO): Long

    @Update
    suspend fun update(request: RequestDTO)

    @Delete
    suspend fun delete(request: RequestDTO)

    @Delete
    suspend fun deleteAll(vararg requests: RequestDTO)
}