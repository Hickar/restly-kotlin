package com.hickar.restly.repository.room

import androidx.annotation.WorkerThread
import com.hickar.restly.mappers.RequestToRequestDTOMapper
import com.hickar.restly.models.Request
import com.hickar.restly.repository.dao.RequestDao

class RequestRepository(private val requestDao: RequestDao) {
    private val mapper: RequestToRequestDTOMapper = RequestToRequestDTOMapper()

    @WorkerThread
    suspend fun getAll(): MutableList<Request> {
        return mapper.toEntityMutableList(requestDao.getAll())
    }

    @WorkerThread
    suspend fun insert(request: Request): Long {
        return requestDao.insert(mapper.toDTO(request))
    }

    @WorkerThread
    suspend fun update(request: Request) {
        return requestDao.update(mapper.toDTO(request))
    }

    @WorkerThread
    suspend fun getById(id: Long): Request {
        val test = requestDao.getById(id)
        return mapper.toEntity(test)
    }
}