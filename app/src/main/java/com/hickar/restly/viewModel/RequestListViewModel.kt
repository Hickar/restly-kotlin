package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.models.Request
import com.hickar.restly.repository.room.RequestRepository
import kotlinx.coroutines.launch

class RequestListViewModel(
    private val repository: RequestRepository,
    private var collectionId: String?
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

    fun refreshRequests() {
        if (collectionId == null) {
            collectionId = Collection.DEFAULT
        }

        viewModelScope.launch {
            requests.value = repository.getByCollectionId(collectionId!!).toMutableList()
        }
    }

    fun deleteRequest(position: Int) {
        viewModelScope.launch {
            repository.delete(requests.value!![position])
            requests.value?.removeAt(position)
            requests.value = requests.value
        }
    }
}