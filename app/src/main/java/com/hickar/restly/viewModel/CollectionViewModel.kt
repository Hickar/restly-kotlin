package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.repository.room.CollectionRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CollectionViewModel(
    private val collectionRepository: CollectionRepository,
) : ViewModel() {

    var collection: Collection = Collection()

    var name: MutableLiveData<String> = MutableLiveData()
    var description: MutableLiveData<String> = MutableLiveData()

    fun loadCollection(collectionId: String?) {
        runBlocking {
            if (collectionId != Collection.DEFAULT && collectionId != null) {
                viewModelScope.launch {
                    collection = collectionRepository.getById(collectionId)
                    collection.let {
                        name.value = collection.name
                        description.value = collection.description
                    }
                }
            }
        }
    }

    fun setName(newName: String) {
        this.name.value = newName
    }

    fun setDescription(newDescription: String) {
        description.value = newDescription
    }

    fun saveCollection() {
        viewModelScope.launch {
            collection.name = name.value!!
            collection.description = description.value!!
            collectionRepository.insert(collection)
        }
    }
}