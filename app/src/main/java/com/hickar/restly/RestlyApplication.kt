package com.hickar.restly

import android.app.Application
import com.hickar.restly.mappers.RequestMapper
import com.hickar.restly.models.Request
import com.hickar.restly.repository.dao.RequestDao
import com.hickar.restly.repository.models.RequestDTO
import com.hickar.restly.repository.room.AppDatabase
import com.hickar.restly.repository.room.BaseRepository
import com.hickar.restly.repository.room.RequestRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.reflect.KProperty

class RestlyApplication : Application() {
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository: RequestRepository = RequestRepository(database.requestDao(), RequestMapper())
}