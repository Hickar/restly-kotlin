package com.hickar.restly.ui.request

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hickar.restly.models.*
import com.hickar.restly.repository.room.RequestRepository
import kotlinx.coroutines.runBlocking

class RequestDetailViewModel(
    private val repository: RequestRepository,
    private val currentRequestId: Long
) : ViewModel() {

    private var currentRequest: Request = Request()
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

            } catch (exception: SQLiteException) {
                Log.e("ViewModel Init Error", exception.toString())
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

    fun addMultipartData() {
        multipartData.value!!.add(RequestMultipartData())
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

    fun getActiveTabPosition(): Int {
        return when(bodyType.value) {
            BodyType.FORMDATA -> TABS.FORMDATA.position
            BodyType.MULTIPART -> TABS.MULTIPART.position
            BodyType.RAW -> TABS.RAW.position
            BodyType.BINARY -> TABS.BINARY.position
            else -> TABS.FORMDATA.position
        }
    }

    fun setActiveTabPosition(position: Int) {
        bodyType.value = when (position) {
            0 -> BodyType.FORMDATA
            1 -> BodyType.MULTIPART
            2 -> BodyType.RAW
            3 -> BodyType.BINARY
            else -> BodyType.NONE
        }
    }

    fun setMethod(newMethod: String) {
        method.value = newMethod
    }

    suspend fun saveRequest() {
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

    fun setBinaryBody(fileMetadata: RequestBinaryData) {
        binaryData.value = fileMetadata
    }

    fun setRawBodyMimeType(mimeType: String) {
        rawData.value!!.mimeType = mimeType
        rawData.value = rawData.value
    }
}

internal enum class TABS(val position: Int) {
    FORMDATA(0),
    MULTIPART(1),
    RAW(2),
    BINARY(3)
}