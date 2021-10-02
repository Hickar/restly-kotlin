package com.hickar.restly.repository.dao

import android.database.sqlite.SQLiteQuery
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

abstract class BaseDao<EntityDTO>(private val databaseName: String) {
    @RawQuery
    protected abstract suspend fun getById(query: SupportSQLiteQuery): EntityDTO

    suspend fun getById(id: Long): EntityDTO {
        val query = SimpleSQLiteQuery("SELECT * FROM $databaseName WHERE id = $id")
        return getById(query)
    }

    @RawQuery
    protected abstract suspend fun getAll(query: SupportSQLiteQuery): MutableList<EntityDTO>

    suspend fun getAll(): MutableList<EntityDTO> {
        val query = SimpleSQLiteQuery("SELECT * FROM $databaseName")
        return getAll(query)
    }

    @Insert
    abstract suspend fun insert(entity: EntityDTO): Long

    @Update
    abstract suspend fun update(entity: EntityDTO)

    @Update
    abstract suspend fun update(vararg entities: EntityDTO)

    @Delete
    abstract suspend fun delete(entity: EntityDTO)

    @Delete
    abstract suspend fun delete(vararg entities: EntityDTO)
}