package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.repository.room.CollectionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class CollectionViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val collectionRepository: CollectionRepository,
) : ViewModel() {

    var collection: Collection = Collection()

    var name: MutableLiveData<String> = MutableLiveData()
    var description: MutableLiveData<String> = MutableLiveData()

    private var collectionId = Collection.DEFAULT

    fun loadCollection(collectionId: String?) {
        if (collectionId != null) {
            this.collectionId = collectionId
        }

        refreshCollection(this.collectionId)
    }

    fun setName(name: String) {
        this.name.value = name
    }

    fun setDescription(description: String) {
        this.description.value = description
    }

    fun saveCollection() {
        viewModelScope.launch {
            collection.name = name.value!!
            collection.description = description.value!!

            collectionRepository.updateCollection(collection)
        }
    }

    fun refreshCurrentCollection() {
        refreshCollection(this.collectionId)
    }

    private fun refreshCollection(id: String) {
        viewModelScope.launch {
            val queriedCollection = collectionRepository.getCollectionById(id)
            if (queriedCollection != null) {
                collection = queriedCollection
            }

            name.value = collection.name
            description.value = collection.description
        }
    }

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): CollectionViewModel
    }
}