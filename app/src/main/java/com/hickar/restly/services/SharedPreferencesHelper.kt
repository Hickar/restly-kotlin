package com.hickar.restly.services

import android.content.Context
import com.google.gson.Gson
import com.hickar.restly.models.PostmanUserInfo

class SharedPreferencesHelper(
    context: Context,
    private val gson: Gson
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getUserInfo(): PostmanUserInfo {
        val json = prefs.getString(USER, "")
        return gson.fromJson(json, PostmanUserInfo::class.java)
    }

    fun setUserInfo(userInfo: PostmanUserInfo) {
        val json = gson.toJson(PostmanUserInfo::class.java)
        prefs.edit().putString(USER, json).apply()
    }

    fun getApiKey(): String? = prefs.getString(POSTMAN_KEY, null)

    fun setApiKey(key: String) = prefs.edit().putString(POSTMAN_KEY, key).apply()

    companion object {
        private const val PREFS_NAME = "com.restly.hickar.preferences"
        private const val USER = "postman_user"
        private const val POSTMAN_KEY = "postman_api_key"
    }
}