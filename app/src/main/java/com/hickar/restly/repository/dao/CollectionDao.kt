package com.hickar.restly.repository.dao

import androidx.room.*
import com.hickar.restly.repository.models.CollectionDTO

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections")
    suspend fun getAll(): List<CollectionDTO>

    @Query("SELECT * FROM collections WHERE origin = \"postman\"")
    suspend fun getAllRemote(): List<CollectionDTO>

    @Query("SELECT * FROM collections WHERE id = :id")
    suspend fun getById(id: String): CollectionDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: CollectionDTO)

    @Update
    suspend fun update(collection: CollectionDTO)

    @Delete
    suspend fun delete(collection: CollectionDTO)
}