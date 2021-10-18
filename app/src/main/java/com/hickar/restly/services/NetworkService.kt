package com.hickar.restly.services

import com.hickar.restly.models.RequestHeader
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class NetworkService {
    private var client: OkHttpClient = OkHttpClient.Builder()
        .build()

    suspend fun requestRaw(
        url: String,
        method: String,
        headers: List<RequestHeader>,
        body: RequestBody?,
        callback: Callback
    ) {
        val builder = Request.Builder()
            .url(url)
            .method(method, body)

        for (header in headers) {
            if (header.enabled) builder.addHeader(header.key, header.valueText)
        }

        val request = builder.build()
        client.newCall(request).enqueue(callback)
    }

    suspend fun get(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "GET", headers, null, callback)
    }

    suspend fun post(url: String, headers: List<RequestHeader>, body: RequestBody, callback: Callback) {
        requestRaw(url, "POST", headers, body, callback)
    }

    suspend fun put(url: String, headers: List<RequestHeader>, body: RequestBody, callback: Callback) {
        requestRaw(url, "PUT", headers, body, callback)
    }

    suspend fun patch(url: String, headers: List<RequestHeader>, body: RequestBody, callback: Callback) {
        requestRaw(url, "PATCH", headers, body, callback)
    }

    suspend fun options(url: String, headers: List<RequestHeader>, body: RequestBody, callback: Callback) {
        requestRaw(url, "OPTIONS", headers, body, callback)
    }

    suspend fun head(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "HEAD", headers, null, callback)
    }

    suspend fun delete(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "DELETE", headers, null, callback)
    }
}