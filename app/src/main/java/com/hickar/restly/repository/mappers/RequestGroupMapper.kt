package com.hickar.restly.repository.mappers

import com.google.gson.Gson
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.repository.models.RequestDirectoryDTO
import javax.inject.Inject

class RequestGroupMapper @Inject constructor(
    private val gson: Gson
) : Mapper<RequestDirectory, RequestDirectoryDTO> {
    override fun toDTO(entity: RequestDirectory): RequestDirectoryDTO {
        return RequestDirectoryDTO(entity.id, entity.name, entity.description, entity.parentId)
    }

    override fun toEntity(dto: RequestDirectoryDTO): RequestDirectory {
        return RequestDirectory(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            parentId = dto.parentId
        )
    }

    override fun toDTOList(entities: List<RequestDirectory>): List<RequestDirectoryDTO> {
        val dtoList = mutableListOf<RequestDirectoryDTO>()
        for (entity in entities) {
            dtoList.add(toDTO(entity))
        }

        return dtoList
    }

    override fun toEntityList(dtos: List<RequestDirectoryDTO>): List<RequestDirectory> {
        val entityList = mutableListOf<RequestDirectory>()
        for (dto in dtos) {
            entityList.add(toEntity(dto))
        }

        return entityList
    }
}