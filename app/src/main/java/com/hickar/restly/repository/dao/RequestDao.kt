package com.hickar.restly.repository.dao

import androidx.room.*
import com.hickar.restly.repository.models.RequestDTO

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getById(id: String): RequestDTO

    @Query("SELECT * FROM requests WHERE parentId = :parentId")
    suspend fun getByGroupId(parentId: String): List<RequestDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: RequestDTO)

    @Update
    suspend fun update(request: RequestDTO)

    @Delete
    suspend fun delete(request: RequestDTO)

    @Query("DELETE FROM requests WHERE parentId = :parentId")
    suspend fun deleteRequestsByGroupId(parentId: String)
}