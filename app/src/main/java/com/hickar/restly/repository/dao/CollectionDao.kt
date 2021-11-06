package com.hickar.restly.repository.dao

import androidx.room.Dao
import com.hickar.restly.repository.models.CollectionDTO

@Dao
abstract class CollectionDao : BaseDao<CollectionDTO>("collections")