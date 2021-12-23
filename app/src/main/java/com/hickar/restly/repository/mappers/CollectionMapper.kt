package com.hickar.restly.repository.mappers

import com.google.gson.Gson
import com.hickar.restly.models.Collection
import com.hickar.restly.repository.models.CollectionDTO
import javax.inject.Inject

class CollectionMapper @Inject constructor(
    private val gson: Gson
) : Mapper<Collection, CollectionDTO> {
    override fun toDTO(entity: Collection): CollectionDTO {
        return CollectionDTO(entity.id, entity.name, entity.description, entity.owner, entity.parentId)
    }

    override fun toEntity(dto: CollectionDTO): Collection {
        return Collection(dto.id, dto.name, dto.description, dto.owner, dto.parentId)
    }

    override fun toDTOList(entities: List<Collection>): List<CollectionDTO> {
        val dtoList = mutableListOf<CollectionDTO>()
        for (entity in entities) {
            dtoList.add(toDTO(entity))
        }

        return dtoList.toMutableList()
    }

    override fun toEntityList(dtos: List<CollectionDTO>): List<Collection> {
        val entityList = mutableListOf<Collection>()
        for (dto in dtos) {
            entityList.add(toEntity(dto))
        }

        return entityList.toMutableList()
    }
}