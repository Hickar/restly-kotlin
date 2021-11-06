package com.hickar.restly.repository.room

import com.hickar.restly.repository.mappers.RequestMapper
import com.hickar.restly.models.Request
import com.hickar.restly.repository.dao.BaseDao
import com.hickar.restly.repository.dao.RequestDao
import com.hickar.restly.repository.models.RequestDTO

class RequestRepository(
    private val requestDao: RequestDao,
    private val mapper: RequestMapper
) : BaseRepository<Request, RequestDTO, BaseDao<RequestDTO>>(
    requestDao,
    mapper
) {
    suspend fun getByCollectionId(collectionId: String): List<Request> {
        return mapper.toEntityList(requestDao.getByCollectionId(collectionId))
    }
}