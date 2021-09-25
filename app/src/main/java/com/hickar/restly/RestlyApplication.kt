package com.hickar.restly

import android.app.Application
import com.hickar.restly.database.AppDatabase

class RestlyApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}