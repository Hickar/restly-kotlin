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
import kotlinx.coroutines.runBlocking

class CollectionViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val collectionRepository: CollectionRepository,
) : ViewModel() {

    var collection: Collection = Collection()

    var name: MutableLiveData<String> = MutableLiveData()
    var description: MutableLiveData<String> = MutableLiveData()

    fun loadCollection(collectionId: String?) {
        runBlocking {
            if (collectionId != Collection.DEFAULT && collectionId != null) {
                viewModelScope.launch {
                    collection = collectionRepository.getCollectionById(collectionId)
                    collection.let {
                        name.value = collection.name
                        description.value = collection.description
                    }
                }
            }
        }
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

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): CollectionViewModel
    }
}