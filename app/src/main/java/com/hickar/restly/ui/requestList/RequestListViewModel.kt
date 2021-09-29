package com.hickar.restly.ui.requests

import androidx.lifecycle.*
import com.hickar.restly.repository.models.Request
import com.hickar.restly.repository.room.RequestRepository

class RequestListViewModel(private val repository: RequestRepository) : ViewModel() {
    val allRequests: LiveData<List<Request>> = repository.allRequests.asLiveData()

    suspend fun createNewDefaultRequest(): Long {
        return repository.insert(Request(0, "GET", "New Request", ""))
    }
}