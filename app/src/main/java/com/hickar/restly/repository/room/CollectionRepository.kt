package com.hickar.restly.repository.room

import androidx.annotation.WorkerThread
import com.hickar.restly.models.Collection
import com.hickar.restly.models.Request
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.repository.dao.CollectionDao
import com.hickar.restly.repository.dao.CollectionRemoteSource
import com.hickar.restly.repository.dao.RequestDao
import com.hickar.restly.repository.dao.RequestGroupDao
import com.hickar.restly.repository.mappers.CollectionMapper
import com.hickar.restly.repository.mappers.RequestGroupMapper
import com.hickar.restly.repository.mappers.RequestMapper
import com.hickar.restly.services.SharedPreferencesHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepository @Inject constructor(
    private val collectionMapper: CollectionMapper,
    private val requestMapper: RequestMapper,
    private val requestGroupMapper: RequestGroupMapper,
    private val collectionDao: CollectionDao,
    private val requestDao: RequestDao,
    private val requestGroupDao: RequestGroupDao,
    private val collectionRemoteSource: CollectionRemoteSource,
    private val prefs: SharedPreferencesHelper
) {
    @WorkerThread
    suspend fun getAllCollections(): List<Collection> {
//        if (prefs.getRestlyUserInfo() != null) {
//            val token = prefs.getRestlyJwt()
//            collectionRemoteSource.getCollections(token) { collections ->
//                MainScope().launch {
//                    for (collection in collections) {
//                        collectionDao.insert(
//                            CollectionDTO(
//                                collection.id,
//                                collection.name,
//                                collection.description,
//                                collection.owner,
//                                null
//                            )
//                        )
//
//                        for (request in collection.items) {
//                            requestDao.insert(requestMapper.toDTO(request))
//                        }
//                    }
//                }
//            }
//        }

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
    suspend fun getRequestGroupById(id: String): RequestDirectory? {
        val requestGroupDto = requestGroupDao.getById(id) ?: return null
        val subgroups = mutableListOf<RequestDirectory>()

        requestGroupDao.getByParentId(requestGroupDto.id).forEach { subgroupDto ->
            val subgroup = getRequestGroupById(subgroupDto.id)
            if (subgroup != null) subgroups.add(subgroup)
        }

        val requests = requestMapper.toEntityList(requestDao.getByGroupId(requestGroupDto.id))

        return RequestDirectory(
            id = requestGroupDto.id,
            name = requestGroupDto.name,
            description = requestGroupDto.description,
            requests = requests.toMutableList(),
            groups = subgroups.toMutableList(),
            parentId = requestGroupDto.parentId
        )
    }

    @WorkerThread
    suspend fun insertRequestGroup(requestGroup: RequestDirectory) {
        requestGroupDao.insert(requestGroupMapper.toDTO(requestGroup))
    }

    @WorkerThread
    suspend fun updateRequestGroup(requestGroup: RequestDirectory) {
        return requestGroupDao.update(requestGroupMapper.toDTO(requestGroup))
    }

    @WorkerThread
    suspend fun deleteRequestGroup(requestGroup: RequestDirectory) {
        requestGroupDao.deleteById(requestGroup.id)
        requestGroupDao.deleteGroupsByParentId(requestGroup.id)
        requestDao.deleteRequestsByGroupId(requestGroup.id)
        for (subgroup in requestGroup.groups) {
            deleteRequestGroup(subgroup)
        }
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
        return requestDao.deleteRequestsByGroupId(id)
    }

    @WorkerThread
    suspend fun saveAllToRemote() {
//        if (prefs.getRestlyUserInfo() != null) {
//            val token = prefs.getRestlyJwt()
//            val collections = collectionDao.getAll()
//            val collectionDTOs = mutableListOf<CollectionRemoteDTO>()
//
//            for (collection in collections) {
//                val requests = requestMapper.toEntityList(requestDao.getByCollectionId(collection.id))
//
//                collectionDTOs.add(
//                    CollectionRemoteDTO(
//                        collection.id,
//                        collection.name,
//                        collection.description,
//                        collection.owner,
//                        requests
//                    )
//                )
//            }
//
//            collectionRemoteSource.postCollections(token, collectionDTOs)
//        }
    }
}