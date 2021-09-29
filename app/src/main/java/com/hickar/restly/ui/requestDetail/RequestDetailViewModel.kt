package com.hickar.restly.ui.requestDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hickar.restly.mappers.RequestToRequestDTOMapper
import com.hickar.restly.models.Request
import com.hickar.restly.repository.room.RequestRepository
import kotlinx.coroutines.runBlocking

class RequestDetailViewModel(
    private val repository: RequestRepository,
    private val currentRequestId: Long
) : ViewModel() {

    val currentRequest: MutableLiveData<Request> = MutableLiveData()

    init {
        runBlocking {
            currentRequest.value = repository.getById(currentRequestId)
        }
    }

}