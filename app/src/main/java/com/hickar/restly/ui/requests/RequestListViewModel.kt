package com.hickar.restly.ui.requests

import androidx.lifecycle.*
import com.hickar.restly.models.Request
import com.hickar.restly.repository.RequestRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class RequestListViewModel(private val repository: RequestRepository) : ViewModel() {
    val allRequests: LiveData<List<Request>> = repository.allRequests.asLiveData()

    suspend fun createNewDefaultRequest(): Long {
        return repository.insert(Request(0, "GET", "New Request", ""))
    }
}