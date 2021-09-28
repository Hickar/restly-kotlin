package com.hickar.restly.repository.room

import androidx.annotation.WorkerThread
import com.hickar.restly.repository.dao.RequestDao
import com.hickar.restly.repository.models.Request
import kotlinx.coroutines.flow.Flow

class RequestRepository(private val requestDao: RequestDao) {
    val allRequests: Flow<List<Request>> = requestDao.getAll()

    @WorkerThread
    suspend fun insert(request: Request): Long {
        return requestDao.insert(request)
    }
}