package com.hickar.restly.repository.room

import androidx.annotation.WorkerThread
import com.hickar.restly.repository.dao.RequestDao
import com.hickar.restly.repository.models.RequestDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

class RequestRepository(private val requestDao: RequestDao) {
    val allRequests: Flow<List<RequestDTO>> = runBlocking {
        requestDao.getAll()
    }

    @WorkerThread
    suspend fun insert(request: RequestDTO): Long {
        return requestDao.insert(request)
    }

    @WorkerThread
    suspend fun getById(id: Long): RequestDTO {
        return requestDao.getById(id)
    }
}