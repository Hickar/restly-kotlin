package com.hickar.restly.database

import androidx.room.*
import com.hickar.restly.models.Request

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests WHERE id = :id")
    fun getById(id: Int): Request

    @Query("SELECT * FROM requests")
    fun getAll(): List<Request>

    @Insert
    fun insert(request: Request)

    @Update
    fun update(request: Request)

    @Delete
    fun delete(request: Request)

    @Delete
    fun deleteAll(vararg requests: Request)
}