package com.hickar.restly.viewModel

import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.extensions.indexOfDiff
import com.hickar.restly.models.*
import com.hickar.restly.repository.room.RequestRepository
import com.hickar.restly.services.ServiceLocator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.*

class RequestViewModel constructor(
    private val repository: RequestRepository
) : ViewModel(), okhttp3.Callback {

    private val fileManager = ServiceLocator.getInstance().getFileManager()

    private lateinit var currentRequest: Request

    val name: MutableLiveData<String> = MutableLiveData()
    val url: MutableLiveData<String> = MutableLiveData()
    val method: MutableLiveData<RequestMethod> = MutableLiveData()
    val params: MutableLiveData<MutableList<RequestQueryParameter>> = MutableLiveData()
    val headers: MutableLiveData<MutableList<RequestHeader>> = MutableLiveData()

    val formData: MutableLiveData<MutableList<RequestFormData>> = MutableLiveData()
    val multipartData: MutableLiveData<MutableList<RequestMultipartData>> = MutableLiveData()
    val rawData: MutableLiveData<RequestRawData> = MutableLiveData()
    val binaryData: MutableLiveData<RequestBinaryData> = MutableLiveData()

    val bodyType: MutableLiveData<BodyType> = MutableLiveData()

    val response: MutableLiveData<com.hickar.restly.models.Response> = MutableLiveData()

    fun loadRequest(requestId: Long) {
        runBlocking {
            try {
                currentRequest = repository.getById(requestId)

                currentRequest.let {
                    name.value = it.name
                    url.value = it.url
                    method.value = it.method
                    params.value = it.queryParams.toMutableList()
                    headers.value = it.headers.toMutableList()
                    formData.value = it.body.formData.toMutableList()
                    multipartData.value = it.body.multipartData.toMutableList()
                    rawData.value = it.body.rawData
                    binaryData.value = it.body.binaryData
                    bodyType.value = it.body.type
                }

            } catch (exception: SQLiteException) {
                Log.e("ViewModel Init Error", exception.toString())
                exception.printStackTrace()
            }
        }
    }

    fun setName(newName: String) {
        name.value = newName
    }

    fun setMethod(method: RequestMethod) {
        this.method.value = method
    }

    fun setUrl(newUrl: String) {
        if (newUrl != url.value!!) {
            syncParamsWithUrl(url.value ?: "", newUrl, params.value!!)
            url.value = newUrl
        }
    }

    private fun syncParamsWithUrl(prevUrl: String, nextUrl: String, params: MutableList<RequestQueryParameter>) {
        val prevUrlParams = parseParams(prevUrl)
        val nextUrlParams = parseParams(nextUrl)

        val enabledParamsIndices = mutableListOf<Int>()
        for (i in params.indices) {
            if (params[i].enabled) enabledParamsIndices.add(i)
        }

        when {
            nextUrlParams.size < prevUrlParams.size -> {
                val paramsStartIndex = nextUrl.indexOf("?")
                if (paramsStartIndex != -1) {
                    val diffIndex = nextUrl.indexOfDiff(prevUrl)
                    val separatorIndices = mutableListOf<Int>()

                    for (c in prevUrl.indices) {
                        if (prevUrl[c] == '&') separatorIndices.add(c)
                    }

                    for (i in separatorIndices.indices) {
                        if (diffIndex == separatorIndices[i]) deleteQueryParameter(i)
                    }
                } else {
                    deleteQueryParameter(0)
                }
            }
            nextUrlParams.size == prevUrlParams.size -> {
                for (i in nextUrlParams.indices) {
                    if (prevUrlParams[i] != nextUrlParams[i]) {
                        setQueryParameterKey(nextUrlParams[i].key, enabledParamsIndices[i])
                        setQueryParameterValue(nextUrlParams[i].valueText, enabledParamsIndices[i])
                    }
                }
            }
            nextUrlParams.size > prevUrlParams.size -> {
                addQueryParameter()
                setQueryParameterKey(nextUrlParams.last().key, nextUrlParams.size - 1)
            }
        }
    }

    private fun parseParams(url: String): MutableList<RequestQueryParameter> {
        val params = mutableListOf<RequestQueryParameter>()
        val paramsStartIndex = url.indexOf("?")

        if (paramsStartIndex != -1) {
            val paramPairs = url.substring(paramsStartIndex + 1, url.length).split("&")

            for (pair in paramPairs) {
                val keyValueList = pair.split("=")
                var key = ""
                var value = ""

                if (keyValueList.size == 1) {
                    key = keyValueList[0]
                }

                if (keyValueList.size == 2) {
                    key = keyValueList[0]
                    value = keyValueList[1]
                }

                params.add(RequestQueryParameter(key, value))
            }
        }

        return params
    }

    fun addQueryParameter() {
        params.value!!.add(RequestQueryParameter())
        params.value = params.value
    }

    fun setQueryParameterKey(text: String, position: Int) {
        if (params.value!![position].key != text) {
            params.value!![position].key = text
            params.value = params.value
        }
    }

    fun setQueryParameterValue(text: String, position: Int) {
        if (params.value!![position].valueText != text) {
            params.value!![position].valueText = text
            params.value = params.value
        }
    }

    fun deleteQueryParameter(position: Int) {
        params.value!!.removeAt(position)
        params.value = params.value
    }

    fun deleteQueryParameter(parameter: RequestQueryParameter) {
        params.value!!.remove(parameter)
        params.value = params.value
    }

    fun toggleQueryParameter(position: Int) {
        params.value!![position].enabled = !params.value?.get(position)!!.enabled
    }

    fun addHeader() {
        headers.value!!.add(RequestHeader())
        headers.value = headers.value
    }

    fun deleteHeader(position: Int) {
        headers.value!!.removeAt(position)
        headers.value = headers.value
    }

    fun toggleHeader(position: Int) {
        headers.value!![position].enabled = !headers.value?.get(position)!!.enabled
    }

    fun getBodyTypeIndex(): Int {
        return when (bodyType.value) {
            BodyType.FORMDATA -> TABS.FORMDATA.position
            BodyType.MULTIPART -> TABS.MULTIPART.position
            BodyType.RAW -> TABS.RAW.position
            BodyType.BINARY -> TABS.BINARY.position
            else -> TABS.NONE.position
        }
    }

    fun setBodyTypeIndex(position: Int) {
        bodyType.value = when (position) {
            TABS.FORMDATA.position -> BodyType.FORMDATA
            TABS.MULTIPART.position -> BodyType.MULTIPART
            TABS.RAW.position -> BodyType.RAW
            TABS.BINARY.position -> BodyType.BINARY
            TABS.NONE.position -> BodyType.NONE
            else -> BodyType.NONE
        }
    }

    fun addFormData() {
        formData.value!!.add(RequestFormData())
        formData.value = formData.value
    }

    fun deleteFormData(position: Int) {
        formData.value!!.removeAt(position)
        formData.value = formData.value
    }

    fun toggleFormData(position: Int) {
        formData.value!![position].enabled = !formData.value?.get(position)!!.enabled
    }

    fun addMultipartData(type: String) {
        multipartData.value!!.add(RequestMultipartData(type = type))
        multipartData.value = multipartData.value
    }

    fun deleteMultipartData(position: Int) {
        multipartData.value!!.removeAt(position)
        multipartData.value = multipartData.value
    }

    fun toggleMultipartData(position: Int) {
        multipartData.value!![position].enabled = !multipartData.value?.get(position)!!.enabled
    }

    fun setMultipartFileBody(position: Int, uri: Uri) {
        multipartData.value!![position].valueFile = fileManager.getRequestFile(uri)!!
        multipartData.value = multipartData.value
    }

    fun setRawBodyMimeType(mimeType: String) {
        rawData.value!!.mimeType = mimeType
        rawData.value = rawData.value
    }

    fun setRawBodyText(textData: String) {
        rawData.value!!.text = textData
        rawData.value = rawData.value
    }

    fun setBinaryBody(uri: Uri) {
        binaryData.value!!.file = fileManager.getRequestFile(uri)!!
        binaryData.value = binaryData.value
    }

    fun getResponseImageBitmap(): Bitmap? {
        return fileManager.getBitmapFromFile(response.value!!.body.file!!)
    }

    fun sendRequest() {
        viewModelScope.launch {
            val networkClient = ServiceLocator.getInstance().getNetworkClient()
            networkClient.sendRequest(currentRequest, this@RequestViewModel)
        }
    }

    fun saveRequest() {
        viewModelScope.launch {
            try {
                currentRequest.name = name.value!!
                currentRequest.url = url.value!!
                currentRequest.method = method.value!!
                currentRequest.queryParams = params.value!!
                currentRequest.headers = headers.value!!
                currentRequest.body.formData = formData.value!!
                currentRequest.body.multipartData = multipartData.value!!
                currentRequest.body.binaryData = binaryData.value!!
                currentRequest.body.type = bodyType.value!!
                repository.insert(currentRequest)
            } catch (exception: SQLiteException) {
                Log.e("Unable to save request", exception.toString())
                exception.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fileManager.deleteFile(response.value?.body?.file)
    }

    override fun onFailure(call: Call, e: IOException) {
        Log.e("RequestViewModel", e.message.toString(), e.cause)
        e.printStackTrace()
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.body != null) {
            val bodySize = if (response.body!!.contentLength() == -1L) 0L else response.body!!.contentLength()
            val contentType = response.body!!.contentType().toString()

            var bodyRawData: String? = null
            var bodyFile: File? = null

            if (
                contentType.contains("text") ||
                contentType.contains("json") ||
                contentType.contains("html")
            ) {
                bodyRawData = response.body!!.string()
            } else {
                val fileManager = ServiceLocator.getInstance().getFileManager()
                bodyFile = fileManager.createTempFile(response.body!!.byteStream())
            }

            val body = ResponseBody(
                response.body!!.contentType().toString(),
                bodySize,
                bodyRawData,
                bodyFile
            )

            this.response.postValue(
                Response(
                    currentRequest.url,
                    response.headers,
                    response.code,
                    Date(response.sentRequestAtMillis),
                    Date(response.receivedResponseAtMillis),
                    response.receivedResponseAtMillis - response.sentRequestAtMillis,
                    response.protocol.toString(),
                    response.isRedirect,
                    body
                )
            )
            response.close()
        }
    }
}

internal enum class TABS(val position: Int) {
    FORMDATA(0),
    MULTIPART(1),
    RAW(2),
    BINARY(3),
    NONE(4)
}