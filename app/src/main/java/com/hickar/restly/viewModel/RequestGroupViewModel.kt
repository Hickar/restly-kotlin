package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.models.Request
import com.hickar.restly.repository.room.CollectionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class RequestGroupViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val repository: CollectionRepository,
) : ViewModel() {

    val requests: MutableLiveData<MutableList<Request>> = MutableLiveData()
    var collectionId: String = Collection.DEFAULT

    fun loadRequests(collectionId: String?) {
        if (collectionId != null) {
            this.collectionId = collectionId
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

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): RequestGroupViewModel
    }
}