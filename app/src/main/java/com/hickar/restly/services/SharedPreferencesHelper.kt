package com.hickar.restly.services

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.google.gson.Gson
import com.hickar.restly.models.PostmanUserInfo
import com.hickar.restly.models.RequestPrefs
import com.hickar.restly.models.WebViewPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class SharedPreferencesHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) {
    private val prefs = FlowSharedPreferences(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE),
        coroutineContext
    )

//    private val restlyUserPrefs = prefs.getNullableString(RESTLY_USER, null)
    private val postmanUserPrefs = prefs.getNullableString(POSTMAN_USER, null)
    private val postmanKeyPrefs = prefs.getNullableString(POSTMAN_KEY, null)

    private val requestPrefs = prefs.getNullableString(REQUEST, null)
    private val webViewPrefs = prefs.getNullableString(WEBVIEW, null)
//    fun getRestlyUserInfo(): Flow<RestlyUserInfo?> {
//        return prefs.getNullableString(RESTLY_USER, null).asFlow().transform { value ->
//            if (value == null) emit(null)
//            else gson.fromJson(value, RestlyUserInfo::class.java)
//        }
//    }
//
//    suspend fun setRestlyUserInfo(userInfo: RestlyUserInfo?) = withContext(coroutineContext) {
//        val json = gson.toJson(userInfo, RestlyUserInfo::class.java)
//        restlyUserPrefs.setAndCommit(json)
//    }
//
//    suspend fun deleteRestlyUserInfo() = withContext(coroutineContext) {
//        restlyUserPrefs.deleteAndCommit()
//    }
//
//    fun getRestlyJwt(): String? {
//        return prefs.getString(RESTLY_JWT, null)
//    }
//
//    fun setRestlyJwt(token: String?) {
//        prefs.edit().putString(RESTLY_JWT, token).apply()
//    }
//
//    fun deleteRestlyJwt() {
//        prefs.edit().remove(RESTLY_JWT).apply()
//    }

    fun getPostmanUserInfo(): Flow<PostmanUserInfo?> {
        return postmanUserPrefs.asFlow().transform {
            if (it == null) emit(null)
            else emit(gson.fromJson(it, PostmanUserInfo::class.java))
        }
    }

    suspend fun setPostmanUserInfo(userInfo: PostmanUserInfo?) = withContext(coroutineContext) {
        val json = gson.toJson(userInfo, PostmanUserInfo::class.java)
        postmanUserPrefs.setAndCommit(json)
    }

    suspend fun deletePostmanUserInfo() = withContext(coroutineContext) {
        postmanUserPrefs.deleteAndCommit()
    }

    fun getPostmanApiKey(): Flow<String?> {
        return postmanKeyPrefs.asFlow()
    }

    suspend fun setPostmanApiKey(key: String) = withContext(coroutineContext) {
        postmanKeyPrefs.setAndCommit(key)
    }

    suspend fun deletePostmanApiKey() = withContext(coroutineContext) {
        postmanKeyPrefs.deleteAndCommit()
    }

    fun getRequestPrefs(): Flow<RequestPrefs> {
        return requestPrefs.asFlow().transform {
            if (it == null) emit(RequestPrefs())
            else emit(gson.fromJson(it, RequestPrefs::class.java))
        }
    }

    suspend fun setRequestPrefs(requestPrefsObj: RequestPrefs) {
        val json = gson.toJson(requestPrefsObj, RequestPrefs::class.java)
        requestPrefs.setAndCommit(json)
    }

    fun getWebViewPrefs(): Flow<WebViewPrefs> {
        return webViewPrefs.asFlow().transform {
            if (it == null) emit(WebViewPrefs())
            else emit(gson.fromJson(it, WebViewPrefs::class.java))
        }
    }

    suspend fun setWebViewPrefs(webViewPrefsObj: WebViewPrefs) = withContext(coroutineContext) {
        val json = gson.toJson(webViewPrefsObj, WebViewPrefs::class.java)
        webViewPrefs.setAndCommit(json)
    }

    companion object {
        private const val PREFS_NAME = "com.restly.hickar.preferences"
//        private const val RESTLY_USER = "restly_user"
//        private const val RESTLY_JWT = "restly_jwt"
        private const val POSTMAN_USER = "postman_user"
        private const val POSTMAN_KEY = "postman_api_key"
        private const val REQUEST = "request"
        private const val WEBVIEW = "webview"
    }
}