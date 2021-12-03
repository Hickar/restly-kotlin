package com.hickar.restly.services

import android.content.Context
import com.google.gson.Gson
import com.hickar.restly.models.PostmanUserInfo
import com.hickar.restly.models.RequestPrefs
import com.hickar.restly.models.RestlyUserInfo
import com.hickar.restly.models.WebViewPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getRestlyUserInfo(): RestlyUserInfo? {
        val json = prefs.getString(RESTLY_USER, null)

        return if (json == null) {
            json
        } else {
            gson.fromJson(json, RestlyUserInfo::class.java)
        }
    }

    fun setRestlyUserInfo(userInfo: RestlyUserInfo?) {
        val json = gson.toJson(userInfo, RestlyUserInfo::class.java)
        prefs.edit().putString(RESTLY_USER, json).apply()
    }

    fun deleteRestlyUserInfo() {
        prefs.edit().remove(RESTLY_USER).apply()
    }

    fun setRestlyJwt(token: String) {
        prefs.edit().putString(RESTLY_JWT, token).apply()
    }

    fun deleteRestlyJwt() {
        prefs.edit().remove(RESTLY_JWT).apply()
    }

    fun getPostmanUserInfo(): PostmanUserInfo? {
        val json = prefs.getString(POSTMAN_USER, null)

        return if (json == null) {
            json
        } else {
            gson.fromJson(json, PostmanUserInfo::class.java)
        }
    }

    fun setPostmanUserInfo(userInfo: PostmanUserInfo?) {
        val json = gson.toJson(userInfo, PostmanUserInfo::class.java)
        prefs.edit().putString(POSTMAN_USER, json).apply()
    }

    fun deletePostmanUserInfo() {
        prefs.edit().remove(POSTMAN_USER).apply()
    }

    fun getPostmanApiKey(): String? = prefs.getString(POSTMAN_KEY, null)

    fun setPostmanApiKey(key: String) = prefs.edit().putString(POSTMAN_KEY, key).apply()

    fun getRequestPrefs(): RequestPrefs {
        val json = prefs.getString(REQUEST, null)

        return if (json == null) {
            RequestPrefs()
        } else {
            gson.fromJson(json, RequestPrefs::class.java)
        }
    }

    fun setRequestPrefs(requestPrefs: RequestPrefs) {
        val json = gson.toJson(requestPrefs, RequestPrefs::class.java)
        prefs.edit().putString(REQUEST, json).apply()
    }

    fun getWebViewPrefs(): WebViewPrefs {
        val json = prefs.getString(WEBVIEW, null)

        return if (json == null) {
            WebViewPrefs()
        } else {
            gson.fromJson(json, WebViewPrefs::class.java)
        }
    }

    fun setWebViewPrefs(webViewPrefs: WebViewPrefs) {
        val json = gson.toJson(webViewPrefs, WebViewPrefs::class.java)
        prefs.edit().putString(WEBVIEW, json).apply()
    }

    companion object {
        private const val PREFS_NAME = "com.restly.hickar.preferences"
        private const val RESTLY_USER = "restly_user"
        private const val RESTLY_JWT = "restly_jwt"
        private const val POSTMAN_USER = "postman_user"
        private const val POSTMAN_KEY = "postman_api_key"
        private const val REQUEST = "request"
        private const val WEBVIEW = "webview"
    }
}