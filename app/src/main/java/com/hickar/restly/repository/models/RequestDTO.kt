package com.hickar.restly.repository.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "requests")
data class RequestDTO(
    @PrimaryKey @NonNull val id: String,
    @NonNull @ColumnInfo(defaultValue = "GET") val method: String,
    @NonNull @ColumnInfo(defaultValue = "New Request") val name: String,
    val query: String,
    val headers: String,
    val body: String,
    val collectionId: String
)