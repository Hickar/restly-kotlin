package com.hickar.restly.mappers

import com.google.gson.Gson
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

    fun toEntity(request: RequestDTO): Request {
        val gson = GsonBuilder().create()

        return Request(
            request.id,
            request.method,
            request.name,
            request.url,
            gson.fromJson(request.queryParams, Array<RequestQueryParameter>::class.java).toMutableList(),
            gson.fromJson(request.headers, Array<RequestHeader>::class.java).toMutableList(),
            gson.fromJson(request.body, RequestBody::class.java)
        )
    }
}