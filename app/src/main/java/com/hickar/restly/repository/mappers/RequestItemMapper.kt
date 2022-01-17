package com.hickar.restly.repository.mappers

import com.google.gson.Gson
import com.hickar.restly.models.Request
import com.hickar.restly.models.RequestItem
import com.hickar.restly.repository.models.RequestItemDTO
import javax.inject.Inject

class RequestItemMapper @Inject constructor(
    private val gson: Gson
) : Mapper<RequestItem, RequestItemDTO> {
    override fun toDTO(entity: RequestItem): RequestItemDTO {
        val requestString = gson.toJson(entity.request)

        return RequestItemDTO(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            request = requestString,
            parentId = entity.parentId
        )
    }

    override fun toDTOList(entities: List<RequestItem>): List<RequestItemDTO> {
        val itemDtoList: MutableList<RequestItemDTO> = mutableListOf()
        for (request in entities) {
            itemDtoList.add(toDTO(request))
        }

        return itemDtoList
    }

    override fun toEntity(entityDTO: RequestItemDTO): RequestItem {
        var request = gson.fromJson(entityDTO.request, Request::class.java)

//        val headers = if (entityDTO.headers.isNotEmpty()) {
//            gson.fromJson(entityDTO.headers, Array<RequestHeader>::class.java).toList()
//        } else {
//            listOf()
//        }

        return RequestItem(
            id = entityDTO.id,
            name = entityDTO.name,
            description = entityDTO.description,
            request = request,
            parentId = entityDTO.parentId
        )
    }

    override fun toEntityList(dtos: List<RequestItemDTO>): List<RequestItem> {
        val entityList: MutableList<RequestItem> = mutableListOf()
        for (request in dtos) {
            entityList.add(toEntity(request))
        }

        return entityList
    }
}