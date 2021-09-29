package com.hickar.restly.ui.requestList

import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.hickar.restly.mappers.RequestToRequestDTOMapper
import com.hickar.restly.models.Request
import com.hickar.restly.models.RequestBody
import com.hickar.restly.models.RequestQueryParameter
import com.hickar.restly.repository.models.RequestDTO
import com.hickar.restly.repository.room.RequestRepository
import kotlinx.coroutines.runBlocking

class RequestListViewModel(
    private val repository: RequestRepository,
) : ViewModel() {

    val requests: MutableLiveData<MutableList<Request>> = MutableLiveData()

    init {
        refreshRequests()
    }

    suspend fun createNewDefaultRequest(): Long {
        val newRequest = Request()
        val newRequestId = repository.insert(newRequest)

        refreshRequests()
        return newRequestId
    }

    private fun refreshRequests() {
        runBlocking {
            requests.value = repository.getAll()
        }
    }
}