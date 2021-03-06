package com.hickar.restly.viewModel

import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import com.hickar.restly.repository.room.CollectionRepository
import com.hickar.restly.services.FileService
import com.hickar.restly.services.NetworkService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import javax.inject.Inject

class RequestViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val repository: CollectionRepository
) : ViewModel() {

    @Inject
    lateinit var fileManager: FileService
    @Inject
    lateinit var networkService: NetworkService

    private lateinit var requestItem: RequestItem
    private lateinit var request: Request

    val name: MutableLiveData<String> = MutableLiveData()
    val method: MutableLiveData<RequestMethod> = MutableLiveData()

    var query: RequestQuery = RequestQuery()
    val url: MutableLiveData<String> = MutableLiveData()
    val queryParameters: MutableLiveData<MutableList<RequestQueryParameter>> = MutableLiveData()

    val headers: MutableLiveData<MutableList<RequestHeader>> = MutableLiveData()

    val formData: MutableLiveData<MutableList<RequestFormData>> = MutableLiveData()
    val multipartData: MutableLiveData<MutableList<RequestMultipartData>> = MutableLiveData()
    val rawData: MutableLiveData<RequestRawData> = MutableLiveData()
    val binaryData: MutableLiveData<RequestBinaryData> = MutableLiveData()

    val bodyType: MutableLiveData<BodyType> = MutableLiveData()

    val response: MutableLiveData<com.hickar.restly.models.Response> = MutableLiveData()

    val error: MutableLiveData<ErrorEvent> = MutableLiveData()

    fun loadRequest(requestId: String) {
        runBlocking {
            try {
                requestItem = repository.getRequestItemById(requestId)
                request = requestItem.request

                requestItem.let {
                    name.value = it.name
                }

                request.let {
                    query = it.query
                    url.value = query.url
                    method.value = it.method
                    queryParameters.value = query.parameters
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
        if (newUrl != query.url) {
            query.setUrl(newUrl)
            queryParameters.postValue(query.parameters)
        }
    }

    fun addQueryParameter() {
        query.addParameter()
        queryParameters.value = query.parameters
        url.value = query.url
    }

    fun setQueryParameterKey(text: String, position: Int) {
        if (query.parameters[position].key != text) {
            query.setParameterKey(position, text)
            url.value = query.url
        }
    }

    fun setQueryParameterValue(text: String, position: Int) {
        if (query.parameters[position].value != text) {
            query.setParameterValue(position, text)
            url.value = query.url
        }
    }

    fun deleteQueryParameter(position: Int) {
        query.deleteParameter(position)
        queryParameters.value = query.parameters
        url.value = query.url
    }

    fun toggleQueryParameter(position: Int) {
        query.toggleParameter(position)
        url.value = query.url
    }

    fun setHeaderKey(position: Int, value: String) {
        headers.value!![position].key = value
    }

    fun setHeaderValue(position: Int, value: String) {
        headers.value!![position].value = value
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
            try {
                val response = networkService.sendRequest(request)
                onSuccess(response)
            } catch (e: IOException) {
                error.postValue(getErrorEvent(e))
            }
        }
    }

    fun saveRequest() {
        viewModelScope.launch {
            request.query = query
            request.method = method.value!!
            request.headers = headers.value!!
            request.body.formData = formData.value!!
            request.body.multipartData = multipartData.value!!
            request.body.binaryData = binaryData.value!!
            request.body.type = bodyType.value!!

            requestItem.name = name.value!!
            requestItem.request = request
            repository.updateRequestItem(requestItem)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fileManager.deleteFile(response.value?.body?.file)
    }

    private fun getErrorEvent(e: IOException): ErrorEvent {
        return when (e) {
            is SocketTimeoutException -> ErrorEvent.ConnectionTimeout
            is ConnectException -> ErrorEvent.ConnectionRefused
            is UnknownHostException -> ErrorEvent.UnknownHostError
            is NetworkUnavailableException -> ErrorEvent.NoInternetConnectionError
            is InterruptedIOException -> ErrorEvent.RequestCallTimeout
            is FileNotFoundException -> ErrorEvent.SizeExceedsLimit
            else -> ErrorEvent.ConnectionUnexpected
        }
    }

    private fun onSuccess(response: Response) {
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
                    request.query.url,
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

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): RequestViewModel
    }
}

internal enum class TABS(val position: Int) {
    FORMDATA(0),
    MULTIPART(1),
    RAW(2),
    BINARY(3),
    NONE(4)
}