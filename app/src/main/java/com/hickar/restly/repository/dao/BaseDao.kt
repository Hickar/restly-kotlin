package com.hickar.restly.repository.dao

import android.database.sqlite.SQLiteException
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlin.jvm.Throws

@Dao
abstract class BaseDao<EntityDTO>(
    private val tableName: String
) {
    @RawQuery
    protected abstract suspend fun internalGetById(query: SupportSQLiteQuery): EntityDTO

    suspend fun getById(id: Long): EntityDTO {
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName WHERE id = $id")
        return internalGetById(query)
    }

    @Throws(SQLiteException::class)
    @RawQuery
    protected abstract suspend fun internalGetAll(query: SupportSQLiteQuery): MutableList<EntityDTO>

    suspend fun getAll(): MutableList<EntityDTO> {
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName")
        return internalGetAll(query)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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