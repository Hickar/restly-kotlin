package com.hickar.restly.repository

import androidx.annotation.WorkerThread
import com.hickar.restly.database.RequestDao
import com.hickar.restly.models.Request
import kotlinx.coroutines.flow.Flow

class RequestRepository(private val requestDao: RequestDao) {
    val allRequests: Flow<List<Request>> = requestDao.getAll()

    @WorkerThread
    suspend fun insert(request: Request): Long {
        return requestDao.insert(request)
    }
}