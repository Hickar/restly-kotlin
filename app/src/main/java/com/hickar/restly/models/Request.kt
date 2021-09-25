package com.hickar.restly.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Header(
    val key: String,
    val value: String
)

@Entity(tableName = "requests")
data class Request(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @NonNull @ColumnInfo(defaultValue = "GET") val method: String,
    @NonNull @ColumnInfo(defaultValue = "New Request") val name: String,
    val url: String,
//    val headers: List<Header>?
)
