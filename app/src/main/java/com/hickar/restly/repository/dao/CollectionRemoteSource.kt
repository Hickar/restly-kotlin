package com.hickar.restly.repository.dao

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.hickar.restly.consts.Paths.Companion.RESTLY_URL_DEV
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import com.hickar.restly.repository.models.CollectionRemoteDTO
import com.hickar.restly.services.NetworkService
import com.hickar.restly.services.SharedPreferencesHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

class CollectionInfo(
    val id: String,
    val name: String,
    val owner: String,
    val uid: String
)

@ExperimentalCoroutinesApi
class CollectionRemoteSource
@Inject constructor(
    private val gson: Gson,
    private val networkService: NetworkService,
    private val prefs: SharedPreferencesHelper,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var token: String? = null

    init {
        coroutineScope.launch {
            prefs.getPostmanApiKey().collect {
                token = it
            }
        }
    }

    suspend fun getCollections(): List<CollectionRemoteDTO> {
        if (token == null) throw IllegalStateException("Jwt is null")
        var request = Request(
            query = RequestQuery(GET_COLLECTIONS_ENDPOINT),
            method = RequestMethod.GET,
            headers = listOf(RequestHeader(HEADER_API_KEY, token!!))
        )

        val collections: MutableList<CollectionRemoteDTO> = mutableListOf()
        var collectionInfoList: List<CollectionInfo> = listOf()

        try {
            var response = networkService.sendRequest(request, shouldGetSize = false)
            when (response.code) {
                200 -> {
                    val body = response.body?.string()
                    response.body?.close()
                    val listType = object : TypeToken<List<CollectionInfo>>() {}.type
                    collectionInfoList = gson.fromJson(body, listType)
                }
                429 -> throw PostmanApiLimitExceeded("Postman API call limit exceeded")
            }

            coroutineScope {
                collectionInfoList.map { infoEntry ->
                    async {
                        request = Request(
                            query = RequestQuery(String.format(GET_COLLECTION_ENDPOINT, infoEntry.uid)),
                            method = RequestMethod.GET,
                            headers = listOf(RequestHeader(HEADER_API_KEY, token!!))
                        )

                        response = networkService.sendRequest(request, shouldGetSize = false)
                        if (response.code == 200) {
                            val body = response.body?.string()
                            response.body?.close()
                            try {
                                val collection = gson.fromJson(body, CollectionRemoteDTO::class.java) ?: return@async
                                collection.owner = infoEntry.owner

                                collections.add(collection)
                            } catch (e: JsonParseException) {
                                Log.e(
                                    "CollectionRemoteSource.getCollections",
                                    "Unexpected error during collection json parsing: ${e.message}",
                                    e
                                )
                            }
                        }
                    }
                }.awaitAll()
            }
        } catch (e: IOException) {
            Log.e("CollectionRemoteSource.getCollections", "Unexpected error: ${e.message}", e)
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

    companion object {
        private const val GET_COLLECTIONS_ENDPOINT = "https://api.getpostman.com/collections"
        private const val GET_COLLECTION_ENDPOINT = "https://api.getpostman.com/collections/%s"

        private const val HEADER_API_KEY = "X-Api-Key"
    }
}