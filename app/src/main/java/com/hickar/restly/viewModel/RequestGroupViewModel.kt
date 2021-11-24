package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.models.Request
import com.hickar.restly.repository.room.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestGroupViewModel @Inject constructor(
    private val repository: CollectionRepository,
) : ViewModel() {

    val requests: MutableLiveData<MutableList<Request>> = MutableLiveData()
    val collectionId: String = Collection.DEFAULT

    fun loadRequests(collectionId: String?) {
        if (collectionId != null) {
            refreshRequests()
        }
    }

    suspend fun createNewDefaultRequest(): String {
        val newRequest = Request(collectionId = collectionId)
        repository.insertRequest(newRequest)

        refreshRequests()
        return newRequest.id
    }

    fun refreshRequests() {
        viewModelScope.launch {
            requests.value = repository.getRequestsByCollectionId(collectionId).toMutableList()
        }
    }

    fun deleteRequest(position: Int) {
        viewModelScope.launch {
            repository.deleteRequest(requests.value!![position])
            requests.value?.removeAt(position)
            requests.value = requests.value
        }
    }
}