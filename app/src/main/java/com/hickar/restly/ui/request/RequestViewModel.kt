package com.hickar.restly.ui.request

import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.consts.RequestMethods
import com.hickar.restly.models.*
import com.hickar.restly.repository.room.RequestRepository
import com.hickar.restly.services.ServiceLocator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

class RequestViewModel(
    private val repository: RequestRepository,
    private val currentRequestId: Long
) : ViewModel(), okhttp3.Callback {

    private lateinit var currentRequest: Request

    val name: MutableLiveData<String> = MutableLiveData()
    val url: MutableLiveData<String> = MutableLiveData()
    val method: MutableLiveData<String> = MutableLiveData()
    val params: MutableLiveData<MutableList<RequestQueryParameter>> = MutableLiveData()
    val headers: MutableLiveData<MutableList<RequestHeader>> = MutableLiveData()

    val formData: MutableLiveData<MutableList<RequestFormData>> = MutableLiveData()
    val multipartData: MutableLiveData<MutableList<RequestMultipartData>> = MutableLiveData()
    val rawData: MutableLiveData<RequestRawData> = MutableLiveData()
    val binaryData: MutableLiveData<RequestBinaryData> = MutableLiveData()

    val bodyType: MutableLiveData<BodyType> = MutableLiveData()

    init {
        runBlocking {
            try {
                currentRequest = repository.getById(currentRequestId)

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

            } catch (e: SQLiteException) {
                Log.e("ViewModel Init Error", e.toString())
                e.printStackTrace()
            }
        }
    }

    fun setName(newName: String) {
        name.value = newName
    }

    fun addQueryParameter() {
        params.value!!.add(RequestQueryParameter())
        params.value = params.value
    }

    fun addHeader() {
        headers.value!!.add(RequestHeader())
        headers.value = headers.value
    }

    fun deleteQueryParameter(position: Int) {
        params.value!!.removeAt(position)
        params.value = params.value
    }

    fun deleteHeader(position: Int) {
        headers.value!!.removeAt(position)
        headers.value = headers.value
    }

    fun toggleParam(position: Int) {
        params.value!![position].enabled = !params.value?.get(position)!!.enabled
    }

    fun toggleHeader(position: Int) {
        headers.value!![position].enabled = !headers.value?.get(position)!!.enabled
    }

    fun toggleFormData(position: Int) {
        formData.value!![position].enabled = !formData.value?.get(position)!!.enabled
    }

    fun toggleMultipartData(position: Int) {
        multipartData.value!![position].enabled = !multipartData.value?.get(position)!!.enabled
    }

    fun addFormData() {
        formData.value!!.add(RequestFormData())
        formData.value = formData.value
    }

    fun addMultipartData(type: String) {
        multipartData.value!!.add(RequestMultipartData(type = type))
        multipartData.value = multipartData.value
    }

    fun deleteFormData(position: Int) {
        formData.value!!.removeAt(position)
        formData.value = formData.value
    }

    fun deleteMultipartData(position: Int) {
        multipartData.value!!.removeAt(position)
        multipartData.value = multipartData.value
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

    fun setMethod(newMethod: String) {
        method.value = newMethod
    }

    fun sendRequest() {
        val client = ServiceLocator.getInstance().getNetworkClient()

        viewModelScope.launch {
            when (method.value) {
                RequestMethods.GET.method -> {
                    client.get(url.value!!, headers.value!!, this@RequestViewModel)
                }
                RequestMethods.POST.method -> {
                    when (bodyType.value!!) {
                        BodyType.FORMDATA -> {
                            client.post(url.value!!, headers.value!!, formData.value!!, this@RequestViewModel)
                        }
                        BodyType.MULTIPART -> {
                            client.post(url.value!!, headers.value!!, multipartData.value!!, this@RequestViewModel)
                        }
                        BodyType.RAW -> {
                            client.post(url.value!!, headers.value!!, rawData.value!!, this@RequestViewModel)
                        }
                        BodyType.BINARY -> {
                            client.post(url.value!!, headers.value!!, binaryData.value!!, this@RequestViewModel)
                        }
                        else -> throw IllegalStateException("BodyType is missing")
                    }
                }
                RequestMethods.PUT.method -> {
                    when (bodyType.value!!) {
                        BodyType.FORMDATA -> {
                            client.put(url.value!!, headers.value!!, formData.value!!, this@RequestViewModel)
                        }
                        BodyType.MULTIPART -> {
                            client.put(url.value!!, headers.value!!, multipartData.value!!, this@RequestViewModel)
                        }
                        BodyType.RAW -> {
                            client.put(url.value!!, headers.value!!, rawData.value!!, this@RequestViewModel)
                        }
                        BodyType.BINARY -> {
                            client.put(url.value!!, headers.value!!, binaryData.value!!, this@RequestViewModel)
                        }
                        else -> throw IllegalStateException("BodyType is missing")
                    }
                }
                RequestMethods.PATCH.method -> {
                    when (bodyType.value!!) {
                        BodyType.FORMDATA -> {
                            client.patch(url.value!!, headers.value!!, formData.value!!, this@RequestViewModel)
                        }
                        BodyType.MULTIPART -> {
                            client.patch(url.value!!, headers.value!!, multipartData.value!!, this@RequestViewModel)
                        }
                        BodyType.RAW -> {
                            client.patch(url.value!!, headers.value!!, rawData.value!!, this@RequestViewModel)
                        }
                        BodyType.BINARY -> {
                            client.patch(url.value!!, headers.value!!, binaryData.value!!, this@RequestViewModel)
                        }
                        else -> throw IllegalStateException("BodyType is missing")
                    }
                }
                RequestMethods.OPTIONS.method -> {
                    when (bodyType.value!!) {
                        BodyType.FORMDATA -> {
                            client.options(url.value!!, headers.value!!, formData.value!!, this@RequestViewModel)
                        }
                        BodyType.MULTIPART -> {
                            client.options(url.value!!, headers.value!!, multipartData.value!!, this@RequestViewModel)
                        }
                        BodyType.RAW -> {
                            client.options(url.value!!, headers.value!!, rawData.value!!, this@RequestViewModel)
                        }
                        BodyType.BINARY -> {
                            client.options(url.value!!, headers.value!!, binaryData.value!!, this@RequestViewModel)
                        }
                        else -> throw IllegalStateException("BodyType is missing")
                    }
                }
                RequestMethods.HEAD.method -> {
                    client.head(url.value!!, headers.value!!, this@RequestViewModel)
                }
                RequestMethods.DELETE.method -> {
                    client.delete(url.value!!, headers.value!!, this@RequestViewModel)
                }
                else -> null
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
                Log.d("ViewModel insert error", exception.toString())
            }
        }
    }

    fun setMultipartFileBody(position: Int, uri: Uri) {
        val fileManager = ServiceLocator.getInstance().getFileManager()
        multipartData.value!![position].valueFile = fileManager.getRequestFile(uri)!!
        multipartData.value = multipartData.value
    }

    fun setBinaryBody(uri: Uri) {
        val fileManager = ServiceLocator.getInstance().getFileManager()
        binaryData.value!!.file = fileManager.getRequestFile(uri)!!
    }

    fun setRawBodyMimeType(mimeType: String) {
        rawData.value!!.mimeType = mimeType
        rawData.value = rawData.value
    }

    fun setRawBodyText(textData: String) {
        rawData.value!!.text = textData
        rawData.value = rawData.value
    }

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
    }

    override fun onResponse(call: Call, response: Response) {
        Log.d("RESPONSE", response.body!!.string())
    }
}

internal enum class TABS(val position: Int) {
    FORMDATA(0),
    MULTIPART(1),
    RAW(2),
    BINARY(3),
    NONE(4)
}