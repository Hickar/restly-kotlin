package com.hickar.restly.ui.requestDetail

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hickar.restly.models.BodyType
import com.hickar.restly.models.Request
import com.hickar.restly.models.RequestBodyBinary
import com.hickar.restly.models.RequestKeyValueParameter
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
    val params: MutableLiveData<MutableList<RequestKeyValueParameter>> = MutableLiveData()
    val headers: MutableLiveData<MutableList<RequestKeyValueParameter>> = MutableLiveData()

    val formData: MutableLiveData<MutableList<RequestKeyValueParameter>> = MutableLiveData()
    val multipartData: MutableLiveData<MutableList<RequestKeyValueParameter>> = MutableLiveData()
    val rawData: MutableLiveData<>
    val binaryData: MutableLiveData<RequestBodyBinary> = MutableLiveData()

    val bodyType: MutableLiveData<BodyType> = MutableLiveData()

    init {
        runBlocking {
            try {
                currentRequest = repository.getById(currentRequestId)

                currentRequest.let {
                    name.value = it.name
                    url.value = it.url
                    method.value = it.method
                    params.value = it.queryParams
                    headers.value = it.headers
                    formData.value = it.body.multipartData.toMutableList()
                    multipartData.value = it.body.formData.toMutableList()
                    binaryData.value = it.body.binaryData
                    bodyType.value = it.body.type
                }

            } catch (exception: SQLiteException) {
                Log.e("ViewModel Init Error", exception.toString())
            }
        }
    }

    fun addQueryParameter() {
        params.value!!.add(RequestKeyValueParameter())
        params.value = params.value
    }

    fun addHeader() {
        headers.value!!.add(RequestKeyValueParameter())
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

    fun toggleUrlEncoded(position: Int) {
        formData.value!![position].enabled = !formData.value?.get(position)!!.enabled
    }

    fun toggleFormData(position: Int) {
        multipartData.value!![position].enabled = !multipartData.value?.get(position)!!.enabled
    }

    fun addUrlEncoded() {
        formData.value!!.add(RequestKeyValueParameter())
        formData.value = formData.value
    }

    fun addFormData() {
        multipartData.value!!.add(RequestKeyValueParameter())
        multipartData.value = multipartData.value
    }

    fun deleteUrlEncoded(position: Int) {
        formData.value!!.removeAt(position)
        formData.value = formData.value
    }

    fun deleteFormData(position: Int) {
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
            currentRequest.body.multipartData = formData.value!!
            currentRequest.body.formData = multipartData.value!!
            currentRequest.body.binaryData = binaryData.value!!
            currentRequest.body.type = bodyType.value!!
            repository.insert(currentRequest)
        } catch (exception: SQLiteException) {
            Log.d("ViewModel insert error", exception.toString())
        }
    }

    fun setBinaryBody(fileMetadata: RequestBodyBinary) {
        binaryData.value = fileMetadata
    }

    fun setRawBodyMimeType(mimeType: String) {

    }
}

internal enum class TABS(val position: Int) {
    FORMDATA(0),
    MULTIPART(1),
    RAW(2),
    BINARY(3)
}