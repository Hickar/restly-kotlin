package com.hickar.restly.services

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hickar.restly.RestlyApplication

class ServiceLocator {
    private lateinit var networkService: NetworkService
    private lateinit var gson: Gson
    private lateinit var fileManager: FileService
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    fun getNetworkClient(): NetworkService {
        if (!this::networkService.isInitialized) {
            networkService = NetworkService(application.contentResolver)
        }

        return networkService
    }

    fun getGson(): Gson {
        if (!this::networkService.isInitialized) {
            gson = GsonBuilder()
                .serializeNulls()
                .create()
        }

        return gson
    }

    fun getFileManager(): FileService {
        if (!this::fileManager.isInitialized) {
            fileManager = FileService(application.applicationContext)
        }

        return fileManager
    }

    fun getSharedPreferences(): SharedPreferencesHelper {
        if (!this::sharedPreferencesHelper.isInitialized) {
            sharedPreferencesHelper = SharedPreferencesHelper(application.applicationContext, gson)
        }

        return sharedPreferencesHelper
    }

    companion object {
        private lateinit var application: RestlyApplication
        private lateinit var instance: ServiceLocator

        fun init(application: RestlyApplication): ServiceLocator {
            this.application = application
            return getInstance()
        }

        fun getInstance(): ServiceLocator {
            if (!this::application.isInitialized) {
                throw InstantiationException("ServiceLocator wasn't initialized")
            }

            if (!this::instance.isInitialized) {
                instance = ServiceLocator()
            }

            return instance
        }
    }
}