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

    val group: MutableLiveData<RequestDirectory> = MutableLiveData()
    val requests: MutableLiveData<MutableList<Request>> = MutableLiveData()
    val folders: MutableLiveData<MutableList<RequestDirectory>> = MutableLiveData()

    private var groupId: String = RequestDirectory.DEFAULT

    fun loadRequestGroup(groupId: String?) {
        if (groupId != null) {
            this.groupId = groupId
        }
        refreshRequestGroup()
    }

    suspend fun createNewDefaultRequest(): String {
        val newRequest = Request(parentId = groupId)
        repository.insertRequest(newRequest)

        refreshRequestGroup()
        return newRequest.id
    }

    suspend fun createNewGroup(): String {
        val newGroup = RequestDirectory(name = "New Folder", parentId = groupId)
        repository.insertRequestGroup(newGroup)

        refreshRequestGroup()
        return newGroup.id
    }

    fun refreshRequestGroup() {
        viewModelScope.launch {
            group.value = repository.getRequestGroupById(groupId)
            if (group.value == null) {
                group.value = RequestDirectory(
                    id = RequestDirectory.DEFAULT,
                    name = "New Folder"
                )
                repository.insertRequestGroup(group.value!!)
            }

            requests.value = group.value?.requests
            folders.value = group.value?.groups
        }
    }

    fun deleteRequest(position: Int) {
        viewModelScope.launch {
            repository.deleteRequest(requests.value!![position])
            group.value?.requests?.removeAt(position)
            group.value = group.value
        }
    }

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): RequestGroupViewModel
    }
}