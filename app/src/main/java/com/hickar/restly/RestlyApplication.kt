package com.hickar.restly

import android.app.Application
import com.hickar.restly.repository.room.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class RestlyApplication : Application() {
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this, applicationScope) }

//    val requestRepository: RequestRepository by lazy {
//        RequestRepository(
//            database.requestDao(),
//            RequestMapper(serviceLocator.getGson())
//        )
//    }
//
//    val collectionRepository: CollectionRepository by lazy {
//        CollectionRepository(
//            database.collectionDao(),
//            CollectionMapper(serviceLocator.getGson())
//        )
//    }
}