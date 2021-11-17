package com.hickar.restly.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
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
    private val context: Context,
) {
    private val contentResolver = context.contentResolver
    private val client: OkHttpClient = OkHttpClient.Builder()
        .build()

    suspend fun requestRaw(
        url: String,
        method: String,
        headers: List<RequestHeader>,
        body: RequestBody?,
        callbackDelegate: Callback
    ) {
        val builder = Request.Builder()
            .url(url)
            .method(method, body)

        for (header in headers) {
            if (header.enabled && !header.isEmpty()) builder.addHeader(header.key, header.value)
        }

        val request = builder.build()
        client.newCall(request).enqueue(callbackDelegate)
    }

    suspend fun sendRequest(request: com.hickar.restly.models.Request, callbackDelegate: Callback) {
        val requestBody = if (request.shouldHaveBody()) {
            createRequestBody(request.body)
        } else {
            null
        }

        requestRaw(request.query.url, request.method.value, request.headers, requestBody, callbackDelegate)
    }

    private fun createRequestBody(body: com.hickar.restly.models.RequestBody?): RequestBody? {
        return when (body?.type) {
            BodyType.FORMDATA -> createFormDataBody(body.formData)
            BodyType.MULTIPART -> createMultipartBody(body.multipartData)
            BodyType.RAW -> createRawBody(body.rawData)
            BodyType.BINARY -> createFileBody(body.binaryData.file)
            else -> null
        }
    }

    private fun createFormDataBody(body: List<RequestFormData>): RequestBody {
        val builder = FormBody.Builder()
        for (item in body) {
            if (item.enabled) builder.addEncoded(item.key, item.value)
        }

        return builder.build()
    }

    private fun createMultipartBody(body: List<RequestMultipartData>): RequestBody {
        val builder = MultipartBody.Builder()
        for (item in body) {
            if (item.enabled) {
                when {
                    item.type == RequestMultipartData.TEXT -> {
                        builder.addFormDataPart(item.key, item.value)
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

        val uri = Uri.parse(file.uri)
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

    fun isNetworkAvailable(): Boolean {
        var hasWifiConnected = false
        var hasCellularConnected = false
        var hasVpnConnected = false

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities != null) {
                hasWifiConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                hasCellularConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                hasVpnConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            }
        } else {
            val networkInfo = cm.activeNetworkInfo
            hasWifiConnected = networkInfo?.type == ConnectivityManager.TYPE_WIFI
            hasCellularConnected = networkInfo?.type == ConnectivityManager.TYPE_MOBILE
            hasVpnConnected = networkInfo?.type == ConnectivityManager.TYPE_VPN
        }

        return hasWifiConnected || hasCellularConnected || hasVpnConnected
    }
}