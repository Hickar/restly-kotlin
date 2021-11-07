package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.repository.room.CollectionRepository
import com.hickar.restly.repository.room.RequestRepository
import kotlinx.coroutines.launch

class CollectionListViewModel(
    private val collectionRepository: CollectionRepository,
    private val requestRepository: RequestRepository
) : ViewModel() {
    val collections: MutableLiveData<MutableList<Collection>> = MutableLiveData()

    init {
        refreshCollections()
    }

    suspend fun createNewCollection(): String {
        val newCollection = Collection()
        collectionRepository.insert(newCollection)

        refreshCollections()
        return newCollection.id
    }

    fun deleteCollection(position: Int) {
        viewModelScope.launch {
            collectionRepository.delete(collections.value!![position])
            requestRepository.deleteByCollectionId(collections.value!![position].id)
            collections.value!!.removeAt(position)
            collections.value = collections.value
        }
    }

    fun refreshCollections() {
        viewModelScope.launch {
            collections.value = collectionRepository.getAll().toMutableList()
        }
    }
}