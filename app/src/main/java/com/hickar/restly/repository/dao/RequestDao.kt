package com.hickar.restly.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.hickar.restly.repository.models.RequestDTO

@Dao
abstract class RequestDao : BaseDao<RequestDTO>("requests") {
    @Query("SELECT * FROM requests WHERE collectionId = :collectionId")
    abstract suspend fun getByCollectionId(collectionId: String): List<RequestDTO>

    @Query("DELETE FROM requests WHERE collectionId = :collectionId")
    abstract suspend fun deleteByCollectionId(collectionId: String)
}