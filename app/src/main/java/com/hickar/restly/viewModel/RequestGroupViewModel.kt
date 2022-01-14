package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    var group: RequestDirectory = RequestDirectory(id = RequestDirectory.DEFAULT, name = "New Folder")

    val name: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String?> = MutableLiveData()
    val requests: MutableLiveData<MutableList<Request>> = MutableLiveData()
    val folders: MutableLiveData<MutableList<RequestDirectory>> = MutableLiveData()

    private var groupId = RequestDirectory.DEFAULT

    fun loadRequestGroup(groupId: String?) {
        if (groupId != null) {
            this.groupId = groupId
        }

        refreshRequestGroup(this.groupId)
    }

    suspend fun createNewDefaultRequest(): String {
        val newRequest = Request(parentId = groupId)
        repository.insertRequest(newRequest)

        refreshRequestGroup(groupId)
        return newRequest.id
    }

    suspend fun createNewGroup(): String {
        val newGroup = RequestDirectory(name = "New Folder", parentId = groupId)
        repository.insertRequestGroup(newGroup)

        refreshRequestGroup(groupId)
        return newGroup.id
    }

    fun refreshCurrentRequestGroup() {
        refreshRequestGroup(groupId)
    }

    private fun refreshRequestGroup(id: String) {
        viewModelScope.launch {
            val queriedGroup = repository.getRequestGroupById(id)
            if (queriedGroup != null) {
                group = queriedGroup
            }

            requests.value = group.requests
            folders.value = group.subgroups
            name.value = group.name
            description.value = group.description
        }
    }

    fun deleteRequest(position: Int) {
        viewModelScope.launch {
            repository.deleteRequest(requests.value!![position])
            requests.value?.removeAt(position)
            requests.value = requests.value
        }
    }

    fun deleteFolder(position: Int) {
        viewModelScope.launch {
            repository.deleteRequestGroup(folders.value!![position])
            folders.value?.removeAt(position)
            folders.value = folders.value
        }
    }

    fun setName(name: String) {
        this.name.value = name
    }

    fun setDescription(description: String) {
        this.description.value = description
    }

    fun saveRequestGroup() {
        viewModelScope.launch {
            group.name = name.value!!
            group.description = description.value
            group.requests = requests.value!!
            group.subgroups = folders.value!!

            repository.updateRequestGroup(group)
        }
    }

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): RequestGroupViewModel
    }
}