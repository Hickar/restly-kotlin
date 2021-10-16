package com.hickar.restly.services

import android.webkit.MimeTypeMap
import com.hickar.restly.models.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URI

class NetworkService {
    private var client: OkHttpClient = OkHttpClient.Builder()
        .build()

    private suspend fun requestRaw(url: String, method: String, headers: List<RequestHeader>, body: RequestBody?, callback: Callback) {
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
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.valueText)
        }

        requestRaw(url, "POST", headers, builder.build(), callback)
    }

    @JvmName("post_multipart")
    suspend fun post(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addFormDataPart(item.key, item.valueText)
        }

        requestRaw(url, "POST", headers, builder.build(), callback)
    }

    @JvmName("post_raw")
    suspend fun post(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        val mediaType = body.mimeType.toMediaType()
        val rawData = body.text.toRequestBody(mediaType)

        requestRaw(url, "POST", headers, rawData, callback)
    }

    @JvmName("post_binary")
    suspend fun post(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        val extension = MimeTypeMap.getFileExtensionFromUrl(body.file?.uri)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        val file = File(URI(body.file?.uri)).asRequestBody(mimeType!!.toMediaType())

        requestRaw(url, "POST", headers, file, callback)
    }

    @JvmName("put_urlencoded")
    suspend fun put(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.valueText)
        }

        requestRaw(url, "PUT", headers, builder.build(), callback)
    }

    @JvmName("put_multipart")
    suspend fun put(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addFormDataPart(item.key, item.valueText)
        }

        requestRaw(url, "PUT", headers, builder.build(), callback)
    }

    @JvmName("put_raw")
    suspend fun put(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        val mediaType = body.mimeType.toMediaType()
        val rawData = body.text.toRequestBody(mediaType)

        requestRaw(url, "PUT", headers, rawData, callback)
    }

    @JvmName("put_binary")
    suspend fun put(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        val mimeType = MimeTypeMap.getFileExtensionFromUrl(url)
        val file = File(body.file!!.uri).asRequestBody(mimeType.toMediaType())

        requestRaw(url, "PUT", headers, file, callback)
    }

    @JvmName("patch_urlencoded")
    suspend fun patch(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.valueText)
        }

        requestRaw(url, "PATCH", headers, builder.build(), callback)
    }

    @JvmName("patch_multipart")
    suspend fun patch(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addFormDataPart(item.key, item.valueText)
        }

        requestRaw(url, "PATCH", headers, builder.build(), callback)
    }

    @JvmName("patch_raw")
    suspend fun patch(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        val mediaType = body.mimeType.toMediaType()
        val rawData = body.text.toRequestBody(mediaType)

        requestRaw(url, "PATCH", headers, rawData, callback)
    }

    @JvmName("patch_binary")
    suspend fun patch(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        val mimeType = MimeTypeMap.getFileExtensionFromUrl(url)
        val file = File(body.file!!.uri).asRequestBody(mimeType.toMediaType())

        requestRaw(url, "PATCH", headers, file, callback)
    }

    @JvmName("options_urlencoded")
    suspend fun options(url: String, headers: List<RequestHeader>, body: List<RequestFormData>, callback: Callback) {
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.valueText)
        }

        requestRaw(url, "OPTIONS", headers, builder.build(), callback)
    }

    @JvmName("options_multipart")
    suspend fun options(url: String, headers: List<RequestHeader>, body: List<RequestMultipartData>, callback: Callback) {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addFormDataPart(item.key, item.valueText)
        }

        requestRaw(url, "OPTIONS", headers, builder.build(), callback)
    }

    @JvmName("options_raw")
    suspend fun options(url: String, headers: List<RequestHeader>, body: RequestRawData, callback: Callback) {
        val mediaType = body.mimeType.toMediaType()
        val rawData = body.text.toRequestBody(mediaType)

        requestRaw(url, "OPTIONS", headers, rawData, callback)
    }

    @JvmName("options_binary")
    suspend fun options(url: String, headers: List<RequestHeader>, body: RequestBinaryData, callback: Callback) {
        val mimeType = MimeTypeMap.getFileExtensionFromUrl(url)
        val file = File(body.file!!.uri).asRequestBody(mimeType.toMediaType())

        requestRaw(url, "OPTIONS", headers, file, callback)
    }

    suspend fun head(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "HEAD", headers, null, callback)
    }

    suspend fun delete(url: String, headers: List<RequestHeader>, callback: Callback) {
        requestRaw(url, "DELETE", headers, null, callback)
    }
}