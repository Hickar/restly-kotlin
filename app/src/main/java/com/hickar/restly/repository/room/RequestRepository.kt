package com.hickar.restly.repository.room

import androidx.annotation.WorkerThread
import com.hickar.restly.mappers.RequestToRequestDTOMapper
import com.hickar.restly.models.Request
import com.hickar.restly.repository.dao.RequestDao

class RequestRepository(private val requestDao: RequestDao) {
    private val mapper: RequestToRequestDTOMapper = RequestToRequestDTOMapper()

//    val allRequests: MutableList<Request> = runBlocking {
//        mapper.toEntityMutableList(requestDao.getAll())
//    }

    @WorkerThread
    suspend fun getAll(): MutableList<Request> {
        return mapper.toEntityMutableList(requestDao.getAll())
    }

    @WorkerThread
    suspend fun insert(request: Request): Long {
        return requestDao.insert(mapper.toDTO(request))
    }

    @WorkerThread
    suspend fun getById(id: Long): Request {
        return mapper.toEntity(requestDao.getById(id))
    }
}