package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.repository.room.CollectionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.util.*

class CollectionListViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val collectionRepository: CollectionRepository,
) : ViewModel() {
    val collections: MutableLiveData<MutableList<Collection>> = MutableLiveData()

    init {
        refreshCollections()
    }

    suspend fun createNewCollection(): String {
        val newCollection = Collection(id = UUID.randomUUID().toString())
        val newRequestGroup = RequestDirectory(id = newCollection.id, name = newCollection.name)

        collectionRepository.insertRequestGroup(newRequestGroup)
        collectionRepository.insertCollection(newCollection)

        refreshCollections()
        return newCollection.id
    }

    fun deleteCollection(position: Int) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(collections.value!![position])
            collectionRepository.deleteRequestsByCollectionId(collections.value!![position].id)
            collections.value!!.removeAt(position)
            collections.value = collections.value
        }
    }

    fun refreshCollections() {
        viewModelScope.launch {
            collections.value = collectionRepository.getAllCollections().toMutableList()
            collectionRepository.saveAllToRemote()
        }
    }

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): CollectionListViewModel
    }
}