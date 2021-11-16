package com.hickar.restly.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hickar.restly.repository.room.RequestRepository

class RequestViewModelFactory(
    private val requestRepository: RequestRepository,
    private val collectionId: String? = null
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RequestViewModel::class.java) -> RequestViewModel(requestRepository) as T
            modelClass.isAssignableFrom(RequestListViewModel::class.java) -> RequestListViewModel(
                requestRepository,
                collectionId
            ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}