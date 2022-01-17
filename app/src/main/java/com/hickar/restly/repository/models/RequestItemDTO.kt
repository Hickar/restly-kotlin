package com.hickar.restly.repository.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "request_items")
data class RequestItemDTO(
    @PrimaryKey @NonNull val id: String,
    @NonNull @ColumnInfo(defaultValue = "New Request") val name: String,
    @NonNull @ColumnInfo(defaultValue = "") val description: String,
    val request: String,
    val parentId: String
)