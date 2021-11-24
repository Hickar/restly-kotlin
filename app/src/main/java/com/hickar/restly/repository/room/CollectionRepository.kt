package com.hickar.restly.repository.room

import androidx.annotation.WorkerThread
import com.hickar.restly.models.Collection
import com.hickar.restly.models.Request
import com.hickar.restly.repository.dao.CollectionDao
import com.hickar.restly.repository.dao.RequestDao
import com.hickar.restly.repository.mappers.CollectionMapper
import com.hickar.restly.repository.mappers.RequestMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepository @Inject constructor(
    private val collectionMapper: CollectionMapper,
    private val requestMapper: RequestMapper,
    private val collectionDao: CollectionDao,
    private val requestDao: RequestDao,
) {
    @WorkerThread
    suspend fun getAllCollections(): List<Collection> {
        return collectionMapper.toEntityList(collectionDao.getAll())
    }

    @WorkerThread
    suspend fun getCollectionById(id: String): Collection {
        return collectionMapper.toEntity(collectionDao.getById(id))
    }

    @WorkerThread
    suspend fun insertCollection(collection: Collection) {
        return collectionDao.insert(collectionMapper.toDTO(collection))
    }

    @WorkerThread
    suspend fun updateCollection(collection: Collection) {
        return collectionDao.update(collectionMapper.toDTO(collection))
    }

    @WorkerThread
    suspend fun deleteCollection(collection: Collection) {
        return collectionDao.delete(collectionMapper.toDTO(collection))
    }

    @WorkerThread
    suspend fun getRequestsByCollectionId(id: String): List<Request> {
        return requestMapper.toEntityList(requestDao.getByCollectionId(id))
    }

    @WorkerThread
    suspend fun getRequestById(id: String): Request {
        return requestMapper.toEntity(requestDao.getById(id))
    }

    @WorkerThread
    suspend fun insertRequest(request: Request) {
        return requestDao.insert(requestMapper.toDTO(request))
    }

    @WorkerThread
    suspend fun updateRequest(request: Request) {
        return requestDao.update(requestMapper.toDTO(request))
    }

    @WorkerThread
    suspend fun deleteRequest(request: Request) {
        return requestDao.delete(requestMapper.toDTO(request))
    }

    @WorkerThread
    suspend fun deleteRequestsByCollectionId(id: String) {
        return requestDao.deleteByCollectionId(id)
    }
}