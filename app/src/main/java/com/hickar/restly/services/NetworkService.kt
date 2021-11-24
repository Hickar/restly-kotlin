package com.hickar.restly.services

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import com.hickar.restly.extensions.toMb
import com.hickar.restly.models.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.BufferedSink
import okio.source
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@InstallIn(SingletonComponent::class)
class NetworkService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferencesHelper
) {
    private val contentResolver = context.contentResolver

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
        val client = if (prefs.getRequestPrefs().sslVerificationEnabled) {
            OkHttpClient.Builder()
                .callTimeout(prefs.getRequestPrefs().timeout, TimeUnit.MILLISECONDS)
                .build()
        } else {
            getUnsafeHttpClient()
        }

        getRemoteFileSize(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callbackDelegate.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                val fileSize = response.body?.contentLength()!!.toMb()
                response.close()
                if (fileSize < prefs.getRequestPrefs().maxSize || prefs.getRequestPrefs().maxSize == 0L) {
                    client.newCall(request).enqueue(callbackDelegate)
                } else {
                    callbackDelegate.onFailure(call, java.io.FileNotFoundException("File size exceeds maximum limit"))
                }
            }
        })
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

    //    https://stackoverflow.com/a/25992879
    private fun getUnsafeHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .callTimeout(prefs.getRequestPrefs().timeout, TimeUnit.MILLISECONDS)
            .build()
    }

    private fun getRemoteFileSize(url: String, callback: Callback) {
        val request = Request.Builder().url(url).method("HEAD", null).build()
        val client = if (prefs.getRequestPrefs().sslVerificationEnabled) {
            OkHttpClient.Builder()
                .callTimeout(prefs.getRequestPrefs().timeout, TimeUnit.MILLISECONDS)
                .build()
        } else {
            getUnsafeHttpClient()
        }
        client.newCall(request).enqueue(callback)
    }
}