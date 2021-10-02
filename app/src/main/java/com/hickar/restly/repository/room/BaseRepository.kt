package com.hickar.restly.repository.room

import android.database.sqlite.SQLiteException
import android.os.strictmode.SqliteObjectLeakedViolation
import android.util.Log
import androidx.annotation.WorkerThread
import com.hickar.restly.mappers.Mapper
import com.hickar.restly.repository.dao.BaseDao
import java.lang.Exception

open class BaseRepository<Entity, EntityDTO, DAO : BaseDao<EntityDTO>>(
    private val dao: DAO,
    private val mapper: Mapper<Entity, EntityDTO>
) {
    @WorkerThread
    suspend fun getAll(): MutableList<Entity> {
        try {
            val test = dao.getAll()
            return mapper.toEntityMutableList(test)
        } catch (exception: SQLiteException) {
            Log.d("ROOM getAll()", exception.toString())
            return mutableListOf()
        } catch (exception: Exception) {
            Log.d("ROOM getAll()", exception.toString())
            return mutableListOf()
        }
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
        val test = dao.getById(id)
        return mapper.toEntity(test)
    }
}