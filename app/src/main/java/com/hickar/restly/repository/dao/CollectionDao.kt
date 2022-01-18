package com.hickar.restly.repository.dao

import androidx.room.*
import com.hickar.restly.repository.models.CollectionDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections")
    fun getAll(): Flow<List<CollectionDTO>>

    @Query("SELECT * FROM collections WHERE origin = \"postman\"")
    suspend fun getAllRemote(): List<CollectionDTO>

    @Query("SELECT * FROM collections WHERE id = :id")
    suspend fun getById(id: String): CollectionDTO?

    @Query ("SELECT EXISTS(SELECT * FROM collections WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: CollectionDTO)

    @Update
    suspend fun update(collection: CollectionDTO)

    @Delete
    suspend fun delete(collection: CollectionDTO)
}