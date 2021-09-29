package com.hickar.restly.repository.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Header(
    val key: String,
    val value: String
)

@Entity(tableName = "requests", primaryKeys = ["id"])
data class Request(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @NonNull @ColumnInfo(defaultValue = "GET") val method: String,
    @NonNull @ColumnInfo(defaultValue = "New Request") val name: String,
    val url: String,
    val queryParams: String,
    val headers: String
)
