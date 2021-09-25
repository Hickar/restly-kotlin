package com.hickar.restly.ui.requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hickar.restly.database.RequestDao
import com.hickar.restly.models.Request

class RequestsViewModel(private val requestDao: RequestDao) : ViewModel() {
    private val requests = requestDao.getAll()

    fun createRequest(method: String, name: String, url: String) {
        requestDao.insert(Request(0, method, name, url))
    }
}