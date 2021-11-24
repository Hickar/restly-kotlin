package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.repository.room.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CollectionListViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
) : ViewModel() {
    val collections: MutableLiveData<MutableList<Collection>> = MutableLiveData()

    init {
        refreshCollections()
    }

    suspend fun createNewCollection(): String {
        val newCollection = Collection(UUID.randomUUID().toString(), UUID.randomUUID().toString())
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
        }
    }
}