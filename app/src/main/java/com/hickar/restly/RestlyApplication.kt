package com.hickar.restly

import android.app.Application
import com.hickar.restly.database.AppDatabase
import com.hickar.restly.repository.RequestRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RestlyApplication : Application() {
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository: RequestRepository by lazy { RequestRepository(database.requestDao()) }
}