package com.hickar.restly.repository.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionDTO(
    @PrimaryKey @NonNull val id: String,
    val name: String,
    val description: String,
    val owner: String
)