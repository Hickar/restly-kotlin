package com.hickar.restly.repository.dao

import androidx.room.*
import com.hickar.restly.repository.models.RequestDirectoryDTO

@Dao
interface RequestGroupDao {
    @Query("SELECT * FROM request_groups WHERE id = :id")
    suspend fun getById(id: String): RequestDirectoryDTO?

    @Query("SELECT * FROM request_groups WHERE parentId = :id")
    suspend fun getByParentId(id: String): List<RequestDirectoryDTO>

    @Insert
    suspend fun insert(requestDirectoryDTO: RequestDirectoryDTO)

    @Update
    suspend fun update(requestDirectoryDTO: RequestDirectoryDTO)

    @Query("DELETE FROM request_groups WHERE id = :id")
    suspend fun deleteById(id: String)

    @Delete
    suspend fun delete(requestDirectoryDTO: RequestDirectoryDTO)

    @Query("DELETE FROM request_groups WHERE parentId = :id")
    suspend fun deleteGroupsByParentId(id: String)
}