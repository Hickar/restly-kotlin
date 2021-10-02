package com.hickar.restly.mappers

import com.google.gson.GsonBuilder
import com.hickar.restly.models.*
import com.hickar.restly.repository.models.RequestDTO

class RequestMapper : Mapper<Request, RequestDTO> {
    override fun toDTO(request: Request): RequestDTO {
        val gson = GsonBuilder().create()

        val params = gson.toJson(request.queryParams)
        val headers = gson.toJson(request.headers)

        return RequestDTO(
            request.id,
            request.method,
            request.name,
            request.url,
            params,
            headers,
            gson.toJson(request.body)
        )
    }

    override fun toDTOMutableList(requests: MutableList<Request>): MutableList<RequestDTO> {
        val dtoList: MutableList<RequestDTO> = mutableListOf()
        for (request in requests) {
            dtoList.add(toDTO(request))
        }

        return dtoList
    }

    override fun toEntity(request: RequestDTO): Request {
        val gson = GsonBuilder().create()

        val queryParams = if (request.queryParams.isNotEmpty()) {
            gson.fromJson(request.queryParams, Array<RequestKeyValue>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        val headers = if (request.headers.isNotEmpty()) {
            gson.fromJson(request.headers, Array<RequestKeyValue>::class.java).toMutableList()
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

    override fun toEntityMutableList(requests: MutableList<RequestDTO>): MutableList<Request> {
        val entityList: MutableList<Request> = mutableListOf()
        for (request in requests) {
            entityList.add(toEntity(request))
        }

        return entityList
    }
}