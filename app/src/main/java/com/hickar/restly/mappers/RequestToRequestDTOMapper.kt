package com.hickar.restly.mappers

import com.google.gson.GsonBuilder
import com.hickar.restly.models.Request
import com.hickar.restly.models.RequestBody
import com.hickar.restly.models.RequestHeader
import com.hickar.restly.models.RequestQueryParameter
import com.hickar.restly.repository.models.RequestDTO

class RequestToRequestDTOMapper {
    fun toDTO(request: Request): RequestDTO {
        val gson = GsonBuilder().create()

        return RequestDTO(
            request.id,
            request.method,
            request.name,
            request.url,
            gson.toJson(request.queryParams),
            gson.toJson(request.headers),
            gson.toJson(request.body)
        )
    }

    fun toDTOMutableList(requests: MutableList<Request>): MutableList<RequestDTO> {
        val dtoList: MutableList<RequestDTO> = mutableListOf()
        for (request in requests) {
            dtoList.add(toDTO(request))
        }

        return dtoList
    }

    fun toEntity(request: RequestDTO): Request {
        val gson = GsonBuilder().create()

        val queryParams = if (request.queryParams.isEmpty()) {
            gson.fromJson(request.queryParams, Array<RequestQueryParameter>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        val headers = if (request.headers.isEmpty()) {
            gson.fromJson(request.headers, Array<RequestHeader>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        return Request(
            request.id,
            request.method,
            request.name,
            request.url,
            queryParams,
            headers,
            gson.fromJson(request.body, RequestBody::class.java)
        )
    }

    fun toEntityMutableList(requests: MutableList<RequestDTO>): MutableList<Request> {
        val entityList: MutableList<Request> = mutableListOf()
        for (request in requests) {
            entityList.add(toEntity(request))
        }

        return entityList
    }
}