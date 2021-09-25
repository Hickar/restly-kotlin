package com.hickar.restly.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hickar.restly.database.RequestDao

class RequestsViewModelFactory(
    private val requestDao: RequestDao
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RequestsViewModel(requestDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel subclass provided")
    }
}