package com.hickar.restly.viewModel

import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import com.hickar.restly.repository.room.RequestRepository
import com.hickar.restly.services.ServiceLocator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.lang.IllegalArgumentException

class RequestViewModel constructor(
    private val repository: RequestRepository
) : ViewModel(), okhttp3.Callback {

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

    fun addQueryParameter() {
        params.value!!.add(RequestQueryParameter())
        params.value = params.value
    }

    fun deleteQueryParameter(position: Int) {
        params.value!!.removeAt(position)
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
        val fileManager = ServiceLocator.getInstance().getFileManager()
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
        val fileManager = ServiceLocator.getInstance().getFileManager()
        binaryData.value!!.file = fileManager.getRequestFile(uri)!!
        binaryData.value = binaryData.value
    }

    fun sendRequest() {
        viewModelScope.launch {
            val client = ServiceLocator.getInstance().getNetworkClient()
            val builder = ServiceLocator.getInstance().getRequestBodyBuilder()
            val requestBody = builder.createRequestBody(currentRequest.body)

            when (method.value) {
                RequestMethod.GET -> {
                    client.get(url.value!!, headers.value!!, this@RequestViewModel)
                }
                RequestMethod.POST -> {
                    client.post(url.value!!, headers.value!!, requestBody!!, this@RequestViewModel)
                }
                RequestMethod.PUT -> {
                    client.put(url.value!!, headers.value!!, requestBody!!, this@RequestViewModel)
                }
                RequestMethod.PATCH -> {
                    client.patch(url.value!!, headers.value!!, requestBody!!, this@RequestViewModel)
                }
                RequestMethod.OPTIONS -> {
                    client.options(url.value!!, headers.value!!, requestBody!!, this@RequestViewModel)
                }
                RequestMethod.HEAD -> {
                    client.head(url.value!!, headers.value!!, this@RequestViewModel)
                }
                RequestMethod.DELETE -> {
                    client.delete(url.value!!, headers.value!!, this@RequestViewModel)
                }
                else -> throw IllegalArgumentException("Non-existent HTTP-method was provided: ${method.value}")
            }
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

    override fun onFailure(call: Call, e: IOException) {
        Log.e("RequestViewModel", e.message.toString(), e.cause)
        e.printStackTrace()
    }

    override fun onResponse(call: Call, response: Response) {
        val body = response.body

        val newResponse = Response(
            currentRequest.url,
            listOf(),
            body?.contentType().toString(),
            body!!.string(),
            response.code
        )
        this.response.postValue(newResponse)

        response.close()
    }
}

internal enum class TABS(val position: Int) {
    FORMDATA(0),
    MULTIPART(1),
    RAW(2),
    BINARY(3),
    NONE(4)
}