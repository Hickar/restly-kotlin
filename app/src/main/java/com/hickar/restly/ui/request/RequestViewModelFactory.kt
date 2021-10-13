package com.hickar.restly.ui.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hickar.restly.repository.room.RequestRepository

class RequestViewModelFactory(
    private val repository: RequestRepository,
    private val requestId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RequestViewModel(repository, requestId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel subclass provided")
    }
}