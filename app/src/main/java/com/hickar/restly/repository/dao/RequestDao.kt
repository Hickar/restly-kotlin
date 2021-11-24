package com.hickar.restly.repository.dao

import androidx.room.*
import com.hickar.restly.repository.models.RequestDTO

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests")
    suspend fun getAll(): List<RequestDTO>

    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getById(id: String): RequestDTO

    @Insert
    suspend fun insert(request: RequestDTO)

    @Update
    suspend fun update(request: RequestDTO)

    @Delete
    suspend fun delete(request: RequestDTO)

    @Query("SELECT * FROM requests WHERE collectionId = :collectionId")
    suspend fun getByCollectionId(collectionId: String): List<RequestDTO>

    @Query("DELETE FROM requests WHERE collectionId = :collectionId")
    suspend fun deleteByCollectionId(collectionId: String)
}