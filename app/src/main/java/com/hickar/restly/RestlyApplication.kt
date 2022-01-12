package com.hickar.restly

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class RestlyApplication : Application() {
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
}