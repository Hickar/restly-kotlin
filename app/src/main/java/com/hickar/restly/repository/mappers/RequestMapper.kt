package com.hickar.restly.repository.mappers

import com.google.gson.Gson
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import com.hickar.restly.repository.models.RequestDTO
import javax.inject.Inject

class RequestMapper @Inject constructor(
    private val gson: Gson
) : Mapper<Request, RequestDTO> {
    override fun toDTO(request: Request): RequestDTO {
        return RequestDTO(
            request.id,
            gson.toJson(request.method),
            request.name,
            gson.toJson(request.query),
            gson.toJson(request.headers),
            gson.toJson(request.body),
            request.parentId
        )
    }

    override fun toDTOList(requests: List<Request>): List<RequestDTO> {
        val dtoList: MutableList<RequestDTO> = mutableListOf()
        for (request in requests) {
            dtoList.add(toDTO(request))
        }

        return dtoList
    }

    override fun toEntity(request: RequestDTO): Request {
        val headers = if (request.headers.isNotEmpty()) {
            gson.fromJson(request.headers, Array<RequestHeader>::class.java).toList()
        } else {
            listOf()
        }

        return Request(
            request.id,
            gson.fromJson(request.method, RequestMethod::class.java),
            request.name,
            gson.fromJson(request.query, RequestQuery::class.java),
            headers,
            gson.fromJson(request.body, RequestBody::class.java),
            request.collectionId
        )
    }

    override fun toEntityList(requests: List<RequestDTO>): List<Request> {
        val entityList: MutableList<Request> = mutableListOf()
        for (request in requests) {
            entityList.add(toEntity(request))
        }

        return entityList
    }
}