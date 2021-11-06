package com.hickar.restly.repository.room

import com.hickar.restly.models.Collection
import com.hickar.restly.repository.dao.BaseDao
import com.hickar.restly.repository.dao.CollectionDao
import com.hickar.restly.repository.mappers.CollectionMapper
import com.hickar.restly.repository.models.CollectionDTO

class CollectionRepository(
    collectionDao: CollectionDao,
    mapper: CollectionMapper
) : BaseRepository<Collection, CollectionDTO, BaseDao<CollectionDTO>>(
    collectionDao,
    mapper
) {
}