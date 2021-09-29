package com.hickar.restly.repository.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "requests", primaryKeys = ["id"])
data class RequestDTO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @NonNull @ColumnInfo(defaultValue = "GET") val method: String,
    @NonNull @ColumnInfo(defaultValue = "New Request") val name: String,
    val url: String,
    val queryParams: String,
    val headers: String,
    val body: String
)
