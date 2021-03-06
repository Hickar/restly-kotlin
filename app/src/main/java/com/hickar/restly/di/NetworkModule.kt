package com.hickar.restly.di

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.google.gson.Gson
import com.hickar.restly.repository.dao.CollectionRemoteSource
import com.hickar.restly.services.NetworkService
import com.hickar.restly.services.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideFlowSharedPreferencesHelper(
        @ApplicationContext context: Context
    ): FlowSharedPreferences {
        return FlowSharedPreferences(
            context.getSharedPreferences("com.restly.hickar.preferences", Context.MODE_PRIVATE),
            Dispatchers.IO
        )
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesHelper(
        @ApplicationContext context: Context,
        gson: Gson,
        flowSharedPreferences: FlowSharedPreferences
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(context, gson, flowSharedPreferences)
    }

    @Singleton
    @Provides
    fun provideNetworkService(
        @ApplicationContext context: Context,
        prefs: SharedPreferencesHelper
    ): NetworkService {
        return NetworkService(context, prefs)
    }

    @Provides
    fun provideCollectionRemoteSource(
        gson: Gson,
        networkService: NetworkService,
        prefs: SharedPreferencesHelper
    ): CollectionRemoteSource {
        return CollectionRemoteSource(gson, networkService, prefs)
    }
}