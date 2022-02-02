package com.hickar.restly.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hickar.restly.models.RequestGroup
import org.jetbrains.annotations.NotNull

@Entity(tableName = "request_groups")
data class RequestDirectoryDTO(
    @PrimaryKey @NotNull val id: String,
    val name: String,
    val description: String?,
    override var parentId: String?,
    val auth: String?
) : RequestGroup()