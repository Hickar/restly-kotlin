package com.hickar.restly.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.Collection
import com.hickar.restly.repository.room.CollectionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CollectionViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val collectionRepository: CollectionRepository,
) : ViewModel() {

    private var collectionId = Collection.DEFAULT
    private var job: Job = Job()

    private var _collection = MutableStateFlow(Collection())
    val collection get() = _collection

    fun loadCollection(collectionId: String?) {
        if (collectionId != null) {
            this.collectionId = collectionId
        }

        refreshCollection(this.collectionId)
    }

    fun setName(name: String) {
        _collection.value.name = name
    }

    fun setDescription(description: String) {
        _collection.value.description = description
    }

    fun saveCollection() {
        viewModelScope.launch {
            collectionRepository.updateCollection(_collection.value)
        }
    }

    fun refreshCurrentCollection() {
        refreshCollection(this.collectionId)
    }

    private fun refreshCollection(id: String) {
        job = viewModelScope.launch {
            ensureActive()
            collectionRepository.getCollectionById(id).collect {
                if (it != null) {
                    _collection.value = it
                }
            }
        }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle): CollectionViewModel
    }
}