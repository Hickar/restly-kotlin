package com.hickar.restly.repository.room

import android.util.Log
import androidx.annotation.WorkerThread
import com.hickar.restly.models.Collection
import com.hickar.restly.models.CollectionOrigin
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.models.RequestItem
import com.hickar.restly.repository.dao.CollectionDao
import com.hickar.restly.repository.dao.CollectionRemoteSource
import com.hickar.restly.repository.dao.RequestGroupDao
import com.hickar.restly.repository.dao.RequestItemDao
import com.hickar.restly.repository.mappers.CollectionMapper
import com.hickar.restly.repository.mappers.RequestGroupMapper
import com.hickar.restly.repository.mappers.RequestItemMapper
import com.hickar.restly.repository.models.CollectionDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class CollectionRepository @Inject constructor(
    private val collectionMapper: CollectionMapper,
    private val requestItemMapper: RequestItemMapper,
    private val requestGroupMapper: RequestGroupMapper,
    private val collectionDao: CollectionDao,
    private val requestItemDao: RequestItemDao,
    private val requestGroupDao: RequestGroupDao,
    private val collectionRemoteSource: CollectionRemoteSource,
) {
    @WorkerThread
    fun getAllCollections(): Flow<List<Collection>> {
        return collectionDao.getAll().transform { dtos ->
            emit(collectionMapper.toEntityList(dtos))
        }
    }

    suspend fun saveRemoteCollections() {
        try {
            val collections = collectionRemoteSource.getCollections()
            for (collection in collections) {
                if (collectionDao.exists(collection.id)) continue

                collectionDao.insert(
                    CollectionDTO(
                        collection.id,
                        collection.name,
                        collection.description,
                        collection.owner,
                        null,
                        origin = CollectionOrigin.POSTMAN.origin
                    )
                )

                if (collection.root != null) {
                    insertRequestGroupAndAllChildren(collection.root!!)
                }
            }
        } catch (e: IllegalStateException) {
            Log.e("CollectionRepository.saveRemoteCollections", "Is not authorized in Postman")
        }
    }

    @WorkerThread
    suspend fun getCollectionById(id: String): Flow<Collection?> {
        return collectionDao.getById(id).transform { collectionDto ->
            if (collectionDto == null) {
                emit(null)
            } else {
                emit(collectionMapper.toEntity(collectionDto))
            }
        }
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
        val rootGroup = getRequestGroupAndChildrenById(collection.id)
        if (rootGroup != null) {
            deleteRequestGroup(rootGroup)
        }
        return collectionDao.delete(collectionMapper.toDTO(collection))
    }

    @WorkerThread
    suspend fun deleteRemoteCollections() {
        return collectionDao.getAllRemote().forEach { collection ->
            deleteCollection(collectionMapper.toEntity(collection))
        }
    }

    //    @WorkerThread
    suspend fun getRequestGroupById(id: String): Flow<RequestDirectory?> {
        val requestGroupFlow = requestGroupDao.getById(id)
        val requestItemFlow = requestItemDao.getByGroupId(id)

        return combine(requestGroupFlow, requestItemFlow) { requestGroupDto, requestItemDtoList ->
            if (requestGroupDto == null) {
                return@combine null
            } else {
                val requests = requestItemMapper.toEntityList(requestItemDtoList)
                val subgroups = mutableListOf<RequestDirectory>()

                coroutineScope {
                    withContext(Dispatchers.IO) {
                        requestGroupDao.getByParentId(requestGroupDto.id).map { subgroupDto ->
                            subgroups.add(requestGroupMapper.toEntity(subgroupDto))
                        }
                    }
                }

                return@combine RequestDirectory(
                    id = requestGroupDto.id,
                    name = requestGroupDto.name,
                    description = requestGroupDto.description,
                    requests = requests.toMutableList(),
                    subgroups = subgroups,
                    parentId = requestGroupDto.parentId
                )
            }
        }
    }

    @WorkerThread
    private suspend fun getRequestGroupAndChildrenById(id: String): RequestDirectory? {
        val requestGroupDto = requestGroupDao.getById(id).firstOrNull() ?: return null
        val subgroups = mutableListOf<RequestDirectory>()

        coroutineScope {
            withContext(Dispatchers.IO) {
                requestGroupDao.getByParentId(requestGroupDto.id).forEach { subgroupDto ->
                    val subgroup = getRequestGroupAndChildrenById(subgroupDto.id)
                    if (subgroup != null) subgroups.add(subgroup)
                }
            }
        }

        val requests = requestItemMapper.toEntityList(requestItemDao.getByGroupId(requestGroupDto.id).first())

        return RequestDirectory(
            id = requestGroupDto.id,
            name = requestGroupDto.name,
            description = requestGroupDto.description,
            requests = requests.toMutableList(),
            subgroups = subgroups.toMutableList(),
            parentId = requestGroupDto.parentId
        )
    }

    @WorkerThread
    suspend fun insertRequestGroup(requestGroup: RequestDirectory) {
        requestGroupDao.insert(requestGroupMapper.toDTO(requestGroup))
    }

    @WorkerThread
    suspend fun insertRequestGroupAndAllChildren(requestGroup: RequestDirectory) {
        insertRequestGroup(requestGroup)
        for (requestItem in requestGroup.requests) {
            insertRequestItem(requestItem)
        }

        for (subgroup in requestGroup.subgroups) {
            insertRequestGroupAndAllChildren(subgroup)
        }
    }

    @WorkerThread
    suspend fun updateRequestGroup(requestGroup: RequestDirectory) {
        return requestGroupDao.update(requestGroupMapper.toDTO(requestGroup))
    }

    @WorkerThread
    suspend fun deleteRequestGroup(requestGroup: RequestDirectory) {
        requestGroupDao.deleteById(requestGroup.id)
        requestGroupDao.deleteGroupsByParentId(requestGroup.id)
        requestItemDao.deleteRequestsByGroupId(requestGroup.id)
        for (subgroup in requestGroup.subgroups) {
            deleteRequestGroup(subgroup)
        }
    }

    @WorkerThread
    suspend fun getRequestItemById(id: String): RequestItem {
        return requestItemMapper.toEntity(requestItemDao.getById(id))
    }

    @WorkerThread
    suspend fun insertRequestItem(request: RequestItem) {
        return requestItemDao.insert(requestItemMapper.toDTO(request))
    }

    @WorkerThread
    suspend fun updateRequestItem(request: RequestItem) {
        return requestItemDao.update(requestItemMapper.toDTO(request))
    }

    @WorkerThread
    suspend fun deleteRequestItem(request: RequestItem) {
        return requestItemDao.delete(requestItemMapper.toDTO(request))
    }

    @WorkerThread
    suspend fun deleteRequestsByCollectionId(id: String) {
        return requestItemDao.deleteRequestsByGroupId(id)
    }

//    @WorkerThread
//    suspend fun saveAllToRemote() {
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
//    }
}