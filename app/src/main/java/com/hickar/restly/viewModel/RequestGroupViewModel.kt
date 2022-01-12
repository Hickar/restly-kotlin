package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.models.Request
import com.hickar.restly.models.RequestDirectory
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
    var groupId: String = Collection.DEFAULT

    fun loadRequests(groupId: String?) {
        if (groupId != null) {
            this.groupId = groupId
            refreshRequests()
        }
    }

    suspend fun createNewDefaultRequest(): String {
        val newRequest = Request(parentId = groupId)
        repository.insertRequest(newRequest)

        refreshRequests()
        return newRequest.id
    }

    suspend fun createNewGroup(): String {
        val newGroup = RequestDirectory(name = "New Folder", parentId = groupId)
//        repository.insertGroup(newGroup)

        refreshRequests()
        return newGroup.id
    }

    fun refreshRequests() {
        viewModelScope.launch {
            requests.value = repository.getRequestsByGroupId(groupId).toMutableList()
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