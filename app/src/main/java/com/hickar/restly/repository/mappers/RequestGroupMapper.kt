package com.hickar.restly.repository.mappers

import com.google.gson.Gson
import com.hickar.restly.models.RequestAuth
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.repository.models.RequestDirectoryDTO
import javax.inject.Inject

class RequestGroupMapper @Inject constructor(
    private val gson: Gson
) : Mapper<RequestDirectory, RequestDirectoryDTO> {
    override fun toDTO(entity: RequestDirectory): RequestDirectoryDTO {
        return RequestDirectoryDTO(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            parentId = entity.parentId,
            auth = gson.toJson(entity.auth)
        )
    }

    override fun toEntity(entityDTO: RequestDirectoryDTO): RequestDirectory {
        return RequestDirectory(
            id = entityDTO.id,
            name = entityDTO.name,
            description = entityDTO.description,
            parentId = entityDTO.parentId,
            auth = gson.fromJson(entityDTO.auth, RequestAuth::class.java)
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