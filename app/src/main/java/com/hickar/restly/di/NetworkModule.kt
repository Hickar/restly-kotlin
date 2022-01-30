package com.hickar.restly.di

import android.content.Context
import com.google.gson.Gson
import com.hickar.restly.repository.dao.CollectionRemoteSource
import com.hickar.restly.services.NetworkService
import com.hickar.restly.services.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideSharedPreferencesHelper(
        @ApplicationContext context: Context,
        gson: Gson
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(context, gson)
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