package com.hickar.restly.repository.room

import com.hickar.restly.mappers.RequestMapper
import com.hickar.restly.models.Request
import com.hickar.restly.repository.dao.BaseDao
import com.hickar.restly.repository.dao.RequestDao
import com.hickar.restly.repository.models.RequestDTO

class RequestRepository(
    requestDao: RequestDao,
    mapper: RequestMapper
) : BaseRepository<Request, RequestDTO, BaseDao<RequestDTO>>(
    requestDao,
    mapper
)