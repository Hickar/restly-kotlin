package com.hickar.restly.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hickar.restly.RestlyApplication

class CollectionViewModelFactory(
    private val application: RestlyApplication,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CollectionViewModel::class.java) -> {
                CollectionViewModel(application.collectionRepository) as T
            }
            modelClass.isAssignableFrom(CollectionListViewModel::class.java) -> {
                CollectionListViewModel(application.collectionRepository, application.requestRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}