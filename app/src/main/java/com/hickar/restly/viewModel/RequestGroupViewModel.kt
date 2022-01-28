package com.hickar.restly.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.models.RequestItem
import com.hickar.restly.repository.room.CollectionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RequestGroupViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val repository: CollectionRepository,
) : ViewModel() {

    private var groupId = RequestDirectory.DEFAULT
    private var job: Job = Job()

    private val _group = MutableStateFlow(RequestDirectory(id = RequestDirectory.DEFAULT))
    val group get() = _group

    fun loadRequestGroup(groupId: String?) {
        if (groupId != null) {
            this.groupId = groupId
        }

        refreshRequestGroup(this.groupId)
    }

    suspend fun createNewDefaultRequest(): String {
        val newRequest = RequestItem(parentId = groupId)
        repository.insertRequestItem(newRequest)

        return newRequest.id
    }

    suspend fun createNewGroup(): String {
        val newGroup = RequestDirectory(name = "New Folder", parentId = groupId)
        repository.insertRequestGroup(newGroup)

        return newGroup.id
    }

    private fun refreshRequestGroup(id: String) {
        job = viewModelScope.launch {
            ensureActive()
            repository.getRequestGroupById(id).collect {
                if (it != null) {
                    _group.value = it
                } else {
                    repository.insertRequestGroup(_group.value)
//                    _group.value = _group.value
                }
            }
        }
//                if (requestGroup != null) {
//                    group = requestGroup
//                } else {
//                    repository.insertRequestGroup(group)
//                }
//
//                requests.value = group.requests
//                folders.value = group.subgroups
//                name.value = group.name
//                description.value = group.description
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    fun deleteRequest(position: Int) {
        viewModelScope.launch {
            val requestItem = group.value.requests[position]
            repository.deleteRequestItem(requestItem)
        }
    }

    fun deleteFolder(position: Int) {
        viewModelScope.launch {
            val subfolder = group.value.subgroups[position]
            repository.deleteRequestGroup(subfolder)
        }
    }

    fun setName(name: String) {
        _group.value.name = name
    }

    fun setDescription(description: String) {
        _group.value.description = description
    }

    fun saveRequestGroup() {
        viewModelScope.launch {
            repository.updateRequestGroup(_group.value)
        }
    }

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): RequestGroupViewModel
    }
}