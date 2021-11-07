package com.hickar.restly.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hickar.restly.RestlyApplication
import com.hickar.restly.repository.room.CollectionRepository

class CollectionViewModelFactory(
    private val application: RestlyApplication,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
//            modelClass.isAssignableFrom(CollectionViewModel::class.java) -> RequestViewModel(requestRepository) as T
            modelClass.isAssignableFrom(CollectionListViewModel::class.java) -> {
                CollectionListViewModel(application.collectionRepository, application.requestRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}