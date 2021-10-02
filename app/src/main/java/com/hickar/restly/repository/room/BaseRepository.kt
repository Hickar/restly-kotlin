package com.hickar.restly.repository.room

import androidx.annotation.WorkerThread
import com.hickar.restly.mappers.Mapper
import com.hickar.restly.repository.dao.BaseDao

open class BaseRepository<Entity, EntityDTO, DAO : BaseDao<EntityDTO>>(
    private val dao: DAO,
    private val mapper: Mapper<Entity, EntityDTO>
) {
    @WorkerThread
    suspend fun getAll(): MutableList<Entity> {
        return mapper.toEntityMutableList(dao.getAll())
    }

    @WorkerThread
    suspend fun insert(entity: Entity): Long {
        return dao.insert(mapper.toDTO(entity))
    }

    @WorkerThread
    suspend fun update(entity: Entity) {
        return dao.update(mapper.toDTO(entity))
    }

    @WorkerThread
    suspend fun getById(id: Long): Entity {
        return mapper.toEntity(dao.getById(id))
    }
}