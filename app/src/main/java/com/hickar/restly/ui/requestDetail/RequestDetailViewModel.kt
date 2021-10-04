package com.hickar.restly.ui.requestDetail

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hickar.restly.models.Request
import com.hickar.restly.models.RequestKeyValue
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
    val params: MutableLiveData<MutableList<RequestKeyValue>> = MutableLiveData()
    val headers: MutableLiveData<MutableList<RequestKeyValue>> = MutableLiveData()
    val urlencodedParams: MutableLiveData<MutableList<RequestKeyValue>> = MutableLiveData()
    val formdataParams: MutableLiveData<MutableList<RequestKeyValue>> = MutableLiveData()

    init {
        runBlocking {
            try {
                currentRequest = repository.getById(currentRequestId)
                name.value = currentRequest.name
                url.value = currentRequest.url
                method.value = currentRequest.method
                params.value = currentRequest.queryParams
                headers.value = currentRequest.headers
                urlencodedParams.value = currentRequest.body.multipartData.toMutableList()
                formdataParams.value = currentRequest.body.formData.toMutableList()
            } catch (exception: SQLiteException) {
                Log.e("ViewModel Init Error", exception.toString())
            }
        }
    }

    fun addQueryParameter() {
        params.value!!.add(RequestKeyValue())
        params.value = params.value
    }

    fun addHeader() {
        headers.value!!.add(RequestKeyValue())
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
        urlencodedParams.value!![position].enabled = !urlencodedParams.value?.get(position)!!.enabled
    }

    fun toggleFormData(position: Int) {
        formdataParams.value!![position].enabled = !formdataParams.value?.get(position)!!.enabled
    }

    fun addUrlEncoded() {
        urlencodedParams.value!!.add(RequestKeyValue())
        urlencodedParams.value = urlencodedParams.value
    }

    fun addFormData() {
        formdataParams.value!!.add(RequestKeyValue())
        formdataParams.value = formdataParams.value
    }

    fun deleteUrlEncoded(position: Int) {
        urlencodedParams.value!!.removeAt(position)
        urlencodedParams.value = urlencodedParams.value
    }

    fun deleteFormData(position: Int) {
        formdataParams.value!!.removeAt(position)
        formdataParams.value = formdataParams.value
    }

    suspend fun saveRequest() {
        try {
            currentRequest.name = name.value!!
            currentRequest.url = url.value!!
            currentRequest.method = method.value!!
            currentRequest.queryParams = params.value!!
            currentRequest.headers = headers.value!!
            currentRequest.body.multipartData = urlencodedParams.value!!
            currentRequest.body.formData = formdataParams.value!!
            repository.insert(currentRequest)
        } catch (exception: SQLiteException) {
            Log.d("ViewModel insert error", exception.toString())
        }
    }
}