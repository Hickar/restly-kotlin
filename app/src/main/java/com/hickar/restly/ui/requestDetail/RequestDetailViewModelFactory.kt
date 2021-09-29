package com.hickar.restly.ui.requestDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hickar.restly.repository.room.RequestRepository

class RequestDetailViewModelFactory(
    private val repository: RequestRepository,
    private val requestId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RequestDetailViewModel(repository, requestId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel subclass provided")
    }
}