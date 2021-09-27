package com.hickar.restly.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hickar.restly.repository.RequestRepository

class RequestListViewModelFactory(
    private val repository: RequestRepository
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RequestListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel subclass provided")
    }
}