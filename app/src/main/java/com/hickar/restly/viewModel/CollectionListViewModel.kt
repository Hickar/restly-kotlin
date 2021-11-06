package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.repository.room.CollectionRepository
import kotlinx.coroutines.launch

class CollectionListViewModel(
    private val repository: CollectionRepository
) : ViewModel() {
    val collections: MutableLiveData<MutableList<Collection>> = MutableLiveData()

    init {
        refreshCollections()
    }

    suspend fun createNewCollection(): String {
        val newCollection = Collection()
        repository.insert(newCollection)

        refreshCollections()
        return newCollection.id
    }

    fun deleteCollection(position: Int) {
        viewModelScope.launch {
            repository.delete(collections.value!![position])
            collections.value!!.removeAt(position)
            collections.value = collections.value
        }
    }

    fun refreshCollections() {
        viewModelScope.launch {
            collections.value = repository.getAll().toMutableList()
        }
    }
}