package com.hickar.restly.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.repository.room.CollectionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalCoroutinesApi
class CollectionListViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val collectionRepository: CollectionRepository,
) : ViewModel() {
    private var job: Job = Job()

    private var _collections = MutableStateFlow(listOf<Collection>())
    val collections get(): StateFlow<List<Collection>> = _collections

    init {
        job = viewModelScope.launch {
            collectionRepository.getAllCollections().collect {
                _collections.value = it
            }
        }
    }

    suspend fun createNewCollection(): String {
        val newCollection = Collection(id = UUID.randomUUID().toString())
        val newRequestGroup = RequestDirectory(id = newCollection.id, name = newCollection.name)

        collectionRepository.insertRequestGroup(newRequestGroup)
        collectionRepository.insertCollection(newCollection)

        return newCollection.id
    }

    fun deleteCollection(position: Int) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(collections.value[position])
        }
    }

    fun forceRemoteCollectionsDownload() {
        viewModelScope.launch { collectionRepository.saveRemoteCollections() }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): CollectionListViewModel
    }
}