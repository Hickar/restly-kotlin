package com.hickar.restly.repository.dao

import androidx.room.*
import com.hickar.restly.repository.models.RequestDirectoryDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestGroupDao {
    @Query("SELECT * FROM request_groups WHERE id = :id")
    fun getById(id: String): Flow<RequestDirectoryDTO?>

    @Query("SELECT * FROM request_groups WHERE parentId = :id")
    fun getByParentId(id: String): List<RequestDirectoryDTO>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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