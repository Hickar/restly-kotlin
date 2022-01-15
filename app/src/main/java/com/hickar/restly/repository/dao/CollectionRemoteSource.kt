package com.hickar.restly.repository.dao

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hickar.restly.consts.Paths.Companion.RESTLY_URL_DEV
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import com.hickar.restly.repository.models.CollectionRemoteDTO
import com.hickar.restly.services.NetworkService
import java.io.IOException
import javax.inject.Inject

class CollectionRemoteSource @Inject constructor(
    private val gson: Gson,
    private val networkService: NetworkService
) {
    suspend fun getCollections(
        token: String?,
    ): List<CollectionRemoteDTO> {
        if (token == null) throw IllegalStateException("Jwt is null")

        val request = Request(
            query = RequestQuery("$RESTLY_URL_DEV/api/collections"),
            method = RequestMethod.GET,
            headers = listOf(RequestHeader("Authorization", "Bearer $token"))
        )

        var collections: List<CollectionRemoteDTO> = listOf()

        try {
            val response = networkService.sendRequest(request)
            if (response.code == 200) {
                val body = response.body?.string()
                val listType = object : TypeToken<List<CollectionRemoteDTO>>() {}.type
                collections = gson.fromJson(body, listType)
            }
        } catch (e: IOException) {
            return listOf()
        }

        return collections
    }

    suspend fun postCollections(token: String?, collectionDTOs: List<CollectionRemoteDTO>) {
        if (token == null) throw IllegalStateException("Jwt is null")

        val listType = object : TypeToken<List<CollectionRemoteDTO>>() {}.type

        val json = gson.toJson(collectionDTOs, listType)
        val request = Request(
            query = RequestQuery("$RESTLY_URL_DEV/api/collections"),
            method = RequestMethod.PATCH,
            headers = listOf(RequestHeader("Authorization", "Bearer $token")),
            body = RequestBody(
                enabled = true,
                type = BodyType.RAW,
                rawData = RequestRawData(
                    json,
                    "application/json"
                )
            )
        )

        networkService.sendRequest(request)
    }
}