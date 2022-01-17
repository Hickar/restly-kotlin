package com.hickar.restly.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.hickar.restly.repository.dao.CollectionDao
import com.hickar.restly.repository.dao.CollectionInfo
import com.hickar.restly.repository.dao.RequestGroupDao
import com.hickar.restly.repository.dao.RequestItemDao
import com.hickar.restly.repository.models.CollectionRemoteDTO
import com.hickar.restly.repository.room.AppDatabase
import com.hickar.restly.utils.CollectionInfoJsonDeserializer
import com.hickar.restly.utils.CollectionJsonDeserializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideRequestDao(database: AppDatabase): RequestItemDao {
        return database.requestItemDao()
    }

    @Provides
    fun provideCollectionDao(database: AppDatabase): CollectionDao {
        return database.collectionDao()
    }

    @Provides
    fun provideRequestGroupDao(database: AppDatabase): RequestGroupDao {
        return database.requestGroupDao()
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        val collectionInfoListType = object : TypeToken<List<CollectionInfo>>() {}.type

        return GsonBuilder()
            .registerTypeAdapter(CollectionRemoteDTO::class.java, CollectionJsonDeserializer())
            .registerTypeAdapter(collectionInfoListType, CollectionInfoJsonDeserializer())
            .disableHtmlEscaping()
            .serializeNulls()
            .create()
    }
}