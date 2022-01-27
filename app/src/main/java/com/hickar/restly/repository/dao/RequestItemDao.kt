package com.hickar.restly.repository.dao

import androidx.room.*
import com.hickar.restly.repository.models.RequestItemDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestItemDao {
    @Query("SELECT * FROM request_items WHERE id = :id")
    suspend fun getById(id: String): RequestItemDTO

    @Query("SELECT * FROM request_items WHERE parentId = :parentId")
    fun getByGroupId(parentId: String): Flow<List<RequestItemDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(requestItem: RequestItemDTO)

    @Update
    suspend fun update(requestItem: RequestItemDTO)

    @Delete
    suspend fun delete(requestItem: RequestItemDTO)

    @Query("DELETE FROM request_items WHERE parentId = :parentId")
    suspend fun deleteRequestsByGroupId(parentId: String)
}