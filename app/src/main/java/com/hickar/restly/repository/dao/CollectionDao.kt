package com.hickar.restly.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.hickar.restly.repository.models.CollectionDTO

@Dao
abstract class CollectionDao : BaseDao<CollectionDTO>("collections") {
    @Query("SELECT * FROM collections WHERE id = :collectionId")
    abstract suspend fun getById(collectionId: String): CollectionDTO
}