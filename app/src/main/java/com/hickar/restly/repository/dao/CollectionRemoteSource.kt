package com.hickar.restly.repository.dao

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hickar.restly.consts.Paths.Companion.RESTLY_URL_DEV
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import com.hickar.restly.repository.models.CollectionRemoteDTO
import com.hickar.restly.services.NetworkService
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class CollectionRemoteSource @Inject constructor(
    private val gson: Gson,
    private val networkService: NetworkService
) {
    suspend fun getCollections(
        token: String?,
        callback: (List<CollectionRemoteDTO>) -> Unit
    ) {
        if (token == null) throw IllegalStateException("Jwt is null")

        val request = Request(
            query = RequestQuery("$RESTLY_URL_DEV/api/collections"),
            method = RequestMethod.GET,
            headers = listOf(RequestHeader("Authorization", "Bearer $token"))
        )

        networkService.sendRequest(request, object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {
                    val body = response.body?.string()
                    val listType = object : TypeToken<List<CollectionRemoteDTO>>() {}.type
                    val collections = gson.fromJson<List<CollectionRemoteDTO>>(body, listType)

                    callback(collections)
                }
            }
        })
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

        networkService.sendRequest(request, object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("postCollections", e.localizedMessage)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("postCollections", "Code: ${response.code}")
            }
        })
    }
}