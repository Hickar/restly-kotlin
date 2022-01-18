package com.hickar.restly.repository.mappers

import com.hickar.restly.models.Collection
import com.hickar.restly.models.CollectionOrigin
import com.hickar.restly.repository.models.CollectionDTO
import javax.inject.Inject

class CollectionMapper @Inject constructor() : Mapper<Collection, CollectionDTO> {
    override fun toDTO(entity: Collection): CollectionDTO {
        return CollectionDTO(entity.id, entity.name, entity.description, entity.owner, entity.parentId, entity.origin.origin)
    }

    override fun toEntity(entityDTO: CollectionDTO): Collection {
        return Collection(
            entityDTO.id,
            entityDTO.name,
            entityDTO.description,
            entityDTO.owner,
            entityDTO.parentId,
            CollectionOrigin.valueOf(entityDTO.origin.uppercase())
        )
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