package com.hickar.restly.ui.requestDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hickar.restly.models.RequestHeader
import com.hickar.restly.models.RequestQueryParameter
import com.hickar.restly.repository.room.RequestRepository
import kotlinx.coroutines.runBlocking

class RequestDetailViewModel(
    private val repository: RequestRepository,
    private val currentRequestId: Long
) : ViewModel() {

    val name: MutableLiveData<String> = MutableLiveData()
    val method: MutableLiveData<String> = MutableLiveData()
    val params: MutableLiveData<MutableList<RequestQueryParameter>> = MutableLiveData()
    val headers: MutableLiveData<MutableList<RequestHeader>> = MutableLiveData()

    init {
        runBlocking {
            val currentRequest = repository.getById(currentRequestId)
            name.value = currentRequest.name
            method.value = currentRequest.method
            params.value = currentRequest.queryParams
            headers.value = currentRequest.headers
        }
    }

}