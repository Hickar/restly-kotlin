package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import com.hickar.restly.services.NetworkService
import com.hickar.restly.services.SharedPreferencesHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class SettingsViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val prefs: SharedPreferencesHelper
) : ViewModel(), okhttp3.Callback {
    @Inject lateinit var networkService: NetworkService
    @Inject lateinit var gson: Gson

    private var isLoggingToRestly: Boolean = false
    val isLoggedInRestly: MutableLiveData<Boolean> = MutableLiveData(false)
    val restlyUserInfo: MutableLiveData<RestlyUserInfo?> = MutableLiveData()

    private var isLoggingToPostman: Boolean = false
    val isLoggedInPostman: MutableLiveData<Boolean> = MutableLiveData(false)
    val postmanUserInfo: MutableLiveData<PostmanUserInfo?> = MutableLiveData()

    val requestPrefs: MutableLiveData<RequestPrefs> = MutableLiveData(prefs.getRequestPrefs())
    val webViewPrefs: MutableLiveData<WebViewPrefs> = MutableLiveData(prefs.getWebViewPrefs())

    val error: MutableLiveData<ErrorEvent> = MutableLiveData()

    private var apiKeyGuess: String = ""

    init {
        val savedPostmanUserInfo = prefs.getPostmanUserInfo()
        if (savedPostmanUserInfo != null) {
            postmanUserInfo.value = savedPostmanUserInfo
            isLoggedInPostman.value = true
        }

        val savedRestlyUserInfo = prefs.getRestlyUserInfo()
        if (savedRestlyUserInfo != null) {
            restlyUserInfo.value = savedRestlyUserInfo
            isLoggedInRestly.value = true
        }

        requestPrefs.value = prefs.getRequestPrefs()
    }

    fun loginToRestly(username: String, password: String) {
        isLoggingToRestly = true

        val credentials = RestlyUserLoginCredentials(username, password)
        val request = Request(
            method = RequestMethod.POST,
            body = RequestBody(
                rawData = RequestRawData(
                    gson.toJson(credentials, RestlyUserLoginCredentials::class.java),
                    "application/json"
                )
            )
        )

        viewModelScope.launch {
            networkService.sendRequest(request, this@SettingsViewModel)
        }
    }

    fun logoutFromRestly() {
        prefs.deleteRestlyUserInfo()
        isLoggedInRestly.value = false
        restlyUserInfo.value = null
    }

    fun signUpInRestly(email: String, username: String, password: String) {

    }

    fun loginToPostman(apiKey: String) {
        isLoggingToPostman = true
        apiKeyGuess = apiKey

        viewModelScope.launch {
            networkService.requestRaw(
                "https://api.getpostman.com/me",
                RequestMethod.GET.value,
                listOf(RequestHeader("X-Api-key", apiKey)),
                null,
                this@SettingsViewModel
            )
        }
    }

    fun logoutFromPostman() {
        prefs.deletePostmanUserInfo()
        isLoggedInPostman.value = false
        postmanUserInfo.value = null
    }

    fun setRequestSslVerificationEnabled(enabled: Boolean) {
        requestPrefs.value?.sslVerificationEnabled = enabled
        prefs.setRequestPrefs(requestPrefs.value!!)
    }

    fun setRequestMaxSize(maxSize: Long) {
        requestPrefs.value?.maxSize = maxSize
        prefs.setRequestPrefs(requestPrefs.value!!)
    }

    fun setRequestTimeout(timeout: Long) {
        requestPrefs.value?.timeout = timeout
        prefs.setRequestPrefs(requestPrefs.value!!)
    }

    fun setWebViewJavascriptEnabled(enabled: Boolean) {
        webViewPrefs.value?.javascriptEnabled = enabled
        prefs.setWebViewPrefs(webViewPrefs.value!!)
    }

    fun setWebViewTextSize(textSize: Int) {
        webViewPrefs.value?.textSize = textSize
        prefs.setWebViewPrefs(webViewPrefs.value!!)
    }

    override fun onFailure(call: Call, e: IOException) {
        val newError = when (e) {
            is SocketTimeoutException -> ErrorEvent.ConnectionTimeout
            is UnknownHostException -> {
                if (networkService.isNetworkAvailable()) {
                    ErrorEvent.UnknownHostError
                } else {
                    ErrorEvent.NoInternetConnectionError
                }
            }
            else -> ErrorEvent.ConnectionUnexpected
        }
        error.postValue(newError)

        if (isLoggingToRestly) isLoggingToRestly = false
        if (isLoggingToPostman) isLoggingToPostman = false
    }

    override fun onResponse(call: Call, response: Response) {
        val responseBody = response.body?.string()

        if (response.code == 200) {
            if (isLoggingToPostman) {
                val info = gson.fromJson(responseBody, PostmanGetMeInfo::class.java)
                postmanUserInfo.postValue(info.user)
                isLoggedInPostman.postValue(true)

                prefs.setApiKey(apiKeyGuess)
                prefs.setPostmanUserInfo(info.user)
                isLoggingToPostman = false
            } else if (isLoggingToRestly) {
                isLoggingToRestly = false
            }
        } else {
            error.postValue(ErrorEvent.AuthenticationError)
        }
    }

    @AssistedFactory
    interface Factory {
        fun build(handle: SavedStateHandle): SettingsViewModel
    }
}