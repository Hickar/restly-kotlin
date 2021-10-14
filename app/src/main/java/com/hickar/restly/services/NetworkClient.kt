package com.hickar.restly.services

import com.hickar.restly.models.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class NetworkClient {
    private fun requestRaw(url: String, method: String, headers: List<RequestHeader>, body: RequestBody?, callback: Callback) {
        val builder = Request.Builder()
            .url(url)
            .method(method, body)

        for (header in headers) {
            if (header.enabled) builder.addHeader(header.key, header.value)
        }

        val request = builder.build()
        client.newCall(request).enqueue(callback)
    }

    fun get(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "GET", headers, null, callback)
    }

    @JvmName("post_urlencoded")
    fun post(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.value)
        }

        requestRaw(url, "POST", headers, builder.build(), callback)
    }

    @JvmName("post_multipart")
    fun post(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addFormDataPart(item.key, item.value)
        }

        requestRaw(url, "POST", headers, builder.build(), callback)
    }

    @JvmName("post_raw")
    fun post(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        val mediaType = body.mimeType.toMediaType()
        val rawData = body.text.toRequestBody(mediaType)

        requestRaw(url, "POST", headers, rawData, callback)
    }

    @JvmName("post_binary")
    fun post(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        val file = File(body.uri).asRequestBody()

        requestRaw(url, "POST", headers, file, callback)
    }

    @JvmName("put_urlencoded")
    fun put(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.value)
        }

        requestRaw(url, "PUT", headers, builder.build(), callback)
    }

    @JvmName("put_multipart")
    fun put(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addFormDataPart(item.key, item.value)
        }

        requestRaw(url, "PUT", headers, builder.build(), callback)
    }

    @JvmName("put_raw")
    fun put(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        val mediaType = body.mimeType.toMediaType()
        val rawData = body.text.toRequestBody(mediaType)

        requestRaw(url, "PUT", headers, rawData, callback)
    }

    @JvmName("put_binary")
    fun put(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        val file = File(body.uri).asRequestBody()

        requestRaw(url, "PUT", headers, file, callback)
    }

    @JvmName("patch_urlencoded")
    fun patch(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.value)
        }

        requestRaw(url, "PATCH", headers, builder.build(), callback)
    }

    @JvmName("patch_multipart")
    fun patch(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addFormDataPart(item.key, item.value)
        }

        requestRaw(url, "PATCH", headers, builder.build(), callback)
    }

    @JvmName("patch_raw")
    fun patch(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        val mediaType = body.mimeType.toMediaType()
        val rawData = body.text.toRequestBody(mediaType)

        requestRaw(url, "PATCH", headers, rawData, callback)
    }

    @JvmName("patch_binary")
    fun patch(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        val file = File(body.uri).asRequestBody()

        requestRaw(url, "PATCH", headers, file, callback)
    }

    @JvmName("options_urlencoded")
    fun options(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.value)
        }

        requestRaw(url, "OPTIONS", headers, builder.build(), callback)
    }

    @JvmName("options_multipart")
    fun options(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addFormDataPart(item.key, item.value)
        }

        requestRaw(url, "OPTIONS", headers, builder.build(), callback)
    }

    @JvmName("options_raw")
    fun options(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        val mediaType = body.mimeType.toMediaType()
        val rawData = body.text.toRequestBody(mediaType)

        requestRaw(url, "OPTIONS", headers, rawData, callback)
    }

    @JvmName("options_binary")
    fun options(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        val file = File(body.uri).asRequestBody()

        requestRaw(url, "OPTIONS", headers, file, callback)
    }

    fun head(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "HEAD", headers, null, callback)
    }

    fun delete(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "DELETE", headers, null, callback)
    }

    companion object {
        private var client = OkHttpClient.Builder()
            .build()

        fun getInstance(): NetworkClient = NetworkClient()
    }
}