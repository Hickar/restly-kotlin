package com.hickar.restly.services

import android.content.ContentResolver
import android.net.Uri.parse
import com.hickar.restly.models.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException

class NetworkService(
    private val contentResolver: ContentResolver
) {
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

    @JvmName("post_urlencoded")
    suspend fun post(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        requestRaw(url, "POST", headers, createFormDataBody(body), callback)
    }

    @JvmName("post_multipart")
    suspend fun post(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        requestRaw(url, "POST", headers, createMultipartBody(body), callback)
    }

    @JvmName("post_raw")
    suspend fun post(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        requestRaw(url, "POST", headers, createRawBody(body), callback)
    }

    @JvmName("post_binary")
    suspend fun post(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        requestRaw(url, "POST", headers, createFileBody(body.file), callback)
    }

    @JvmName("put_urlencoded")
    suspend fun put(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        requestRaw(url, "PUT", headers, createFormDataBody(body), callback)
    }

    @JvmName("put_multipart")
    suspend fun put(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        requestRaw(url, "PUT", headers, createMultipartBody(body), callback)
    }

    @JvmName("put_raw")
    suspend fun put(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        requestRaw(url, "PUT", headers, createRawBody(body), callback)
    }

    @JvmName("put_binary")
    suspend fun put(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        requestRaw(url, "POST", headers, createFileBody(body.file), callback)
    }

    @JvmName("patch_urlencoded")
    suspend fun patch(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        requestRaw(url, "PATCH", headers, createFormDataBody(body), callback)
    }

    @JvmName("patch_multipart")
    suspend fun patch(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        requestRaw(url, "PATCH", headers, createMultipartBody(body), callback)
    }

    @JvmName("patch_raw")
    suspend fun patch(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        requestRaw(url, "PATCH", headers, createRawBody(body), callback)
    }

    @JvmName("patch_binary")
    suspend fun patch(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        requestRaw(url, "PATCH", headers, createFileBody(body.file), callback)
    }

    @JvmName("options_urlencoded")
    suspend fun options(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        requestRaw(url, "OPTIONS", headers, createFormDataBody(body), callback)
    }

    @JvmName("options_multipart")
    suspend fun options(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        requestRaw(url, "OPTIONS", headers, createMultipartBody(body), callback)
    }

    @JvmName("options_raw")
    suspend fun options(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        requestRaw(url, "OPTIONS", headers, createRawBody(body), callback)
    }

    @JvmName("options_binary")
    suspend fun options(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        requestRaw(url, "OPTIONS", headers, createFileBody(body.file), callback)
    }

    suspend fun head(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "HEAD", headers, null, callback)
    }

    suspend fun delete(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "DELETE", headers, null, callback)
    }

    private fun createFormDataBody(body: List<RequestFormData>): RequestBody {
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.valueText)
        }

        return builder.build()
    }

    private fun createMultipartBody(body: List<RequestMultipartData>): RequestBody {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) {
                when {
                    item.type == RequestMultipartData.TEXT -> {
                        builder.addFormDataPart(item.key, item.valueText)
                    }
                    item.type == RequestMultipartData.FILE && item.valueFile != null -> {
                        builder.addFormDataPart("file", item.key, createFileBody(item.valueFile)!!)
                    }
                    else -> {
                    }
                }
            }
        }

        return builder.build()
    }

    private fun createFileBody(file: RequestFile?): RequestBody? {
        if (file == null) {
            return null
        }

        val uri = parse(file.uri)
        val inputStream = contentResolver.openInputStream(uri)

        return object : RequestBody() {
            override fun contentType(): MediaType {
                return file.mimeType.toMediaType()
            }

            override fun writeTo(sink: BufferedSink) {
                val source = inputStream!!.source()

                try {
                    sink.writeAll(source)
                } finally {
                    source.close()
                }
            }

            override fun contentLength(): Long {
                return try {
                    inputStream!!.available().toLong()
                } catch (e: IOException) {
                    0
                }
            }
        }
    }

    private fun createRawBody(body: RequestRawData): RequestBody {
        return body.text.toRequestBody(body.mimeType.toMediaType())
    }
}