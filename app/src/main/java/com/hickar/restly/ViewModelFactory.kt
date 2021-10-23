package com.hickar.restly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hickar.restly.repository.room.RequestRepository
import com.hickar.restly.viewModel.RequestViewModel
import com.hickar.restly.viewModel.RequestListViewModel

class ViewModelFactory(
    private val requestRepository: RequestRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RequestViewModel::class.java) -> RequestViewModel(requestRepository) as T
            modelClass.isAssignableFrom(RequestListViewModel::class.java) -> RequestListViewModel(requestRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: RestlyApplication): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = ViewModelFactory(
                            application.repository,
                        )
                    }
                }
            }
            return INSTANCE ?: throw NullPointerException("Expression 'INSTANCE' must not be null")
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}