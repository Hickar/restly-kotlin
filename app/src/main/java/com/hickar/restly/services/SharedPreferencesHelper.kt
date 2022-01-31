package com.hickar.restly.services

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.google.gson.Gson
import com.hickar.restly.models.PostmanUserInfo
import com.hickar.restly.models.RequestPrefs
import com.hickar.restly.models.WebViewPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class SharedPreferencesHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val prefs: FlowSharedPreferences
) {
    private val postmanUserPrefs = prefs.getNullableString(POSTMAN_USER, null)
    private val postmanKeyPrefs = prefs.getNullableString(POSTMAN_KEY, null)

    private val requestPrefs = prefs.getNullableString(REQUEST, null)
    private val webViewPrefs = prefs.getNullableString(WEBVIEW, null)

    fun getPostmanUserInfo(): Flow<PostmanUserInfo?> {
        return postmanUserPrefs.asFlow().transform {
            if (it == null) emit(null)
            else emit(gson.fromJson(it, PostmanUserInfo::class.java))
        }
    }

    suspend fun setPostmanUserInfo(userInfo: PostmanUserInfo?): Boolean {
        val json = gson.toJson(userInfo, PostmanUserInfo::class.java)
        return postmanUserPrefs.setAndCommit(json)
    }

    suspend fun deletePostmanUserInfo(): Boolean {
        return postmanUserPrefs.deleteAndCommit()
    }

    fun getPostmanApiKey(): Flow<String?> {
        return postmanKeyPrefs.asFlow()
    }

    suspend fun setPostmanApiKey(key: String): Boolean {
        return postmanKeyPrefs.setAndCommit(key)
    }

    suspend fun deletePostmanApiKey(): Boolean {
        return postmanKeyPrefs.deleteAndCommit()
    }

    fun getRequestPrefs(): Flow<RequestPrefs> {
        return requestPrefs.asFlow().transform {
            if (it == null) emit(RequestPrefs())
            else emit(gson.fromJson(it, RequestPrefs::class.java))
        }
    }

    suspend fun setRequestPrefs(requestPrefsObj: RequestPrefs): Boolean {
        val json = gson.toJson(requestPrefsObj, RequestPrefs::class.java)
        return requestPrefs.setAndCommit(json)
    }

    fun getWebViewPrefs(): Flow<WebViewPrefs> {
        return webViewPrefs.asFlow().transform {
            if (it == null) emit(WebViewPrefs())
            else emit(gson.fromJson(it, WebViewPrefs::class.java))
        }
    }

    suspend fun setWebViewPrefs(webViewPrefsObj: WebViewPrefs): Boolean {
        val json = gson.toJson(webViewPrefsObj, WebViewPrefs::class.java)
        return webViewPrefs.setAndCommit(json)
    }

    companion object {
        private const val PREFS_NAME = "com.restly.hickar.preferences"
        private const val POSTMAN_USER = "postman_user"
        private const val POSTMAN_KEY = "postman_api_key"
        private const val REQUEST = "request"
        private const val WEBVIEW = "webview"
    }
}