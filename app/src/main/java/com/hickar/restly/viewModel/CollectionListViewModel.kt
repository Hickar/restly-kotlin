package com.hickar.restly.viewModel

import androidx.lifecycle.*
import com.hickar.restly.models.Collection
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.repository.room.CollectionRepository
import com.hickar.restly.services.SharedPreferencesHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class CollectionListViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val collectionRepository: CollectionRepository,
    private val prefs: SharedPreferencesHelper
) : ViewModel() {
    private val collectionJob: Job
    var collections: LiveData<List<Collection>> = MutableLiveData()

    init {
        collectionJob = viewModelScope.launch {
            collectionRepository.getAllCollections()
            collections = collectionRepository
                .getAllCollections()
                .cancellable()
                .asLiveData()
        }

        collectionJob.start()
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
            collectionRepository.deleteCollection(collections.value!![position])
            collectionRepository.deleteRequestsByCollectionId(collections.value!![position].id)
        }
    }

    fun forceRemoteCollectionsDownload() {
        viewModelScope.launch { collectionRepository.saveRemoteCollections() }
    }

    fun cancelCollectionsPolling() {
        if (collectionJob.isActive) {
            collectionJob.cancel()
        }
    }

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): CollectionListViewModel
    }
}