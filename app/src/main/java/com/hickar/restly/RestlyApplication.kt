package com.hickar.restly

import android.app.Application
import com.hickar.restly.repository.mappers.CollectionMapper
import com.hickar.restly.repository.mappers.RequestMapper
import com.hickar.restly.repository.room.AppDatabase
import com.hickar.restly.repository.room.CollectionRepository
import com.hickar.restly.repository.room.RequestRepository
import com.hickar.restly.services.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RestlyApplication : Application() {
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val serviceLocator = ServiceLocator.init(this)

    val requestRepository: RequestRepository by lazy {
        RequestRepository(
            database.requestDao(),
            RequestMapper(serviceLocator.getGson())
        )
    }

    val collectionRepository: CollectionRepository by lazy {
        CollectionRepository(
            database.collectionDao(),
            CollectionMapper(serviceLocator.getGson())
        )
    }
}