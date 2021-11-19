package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import com.hickar.restly.services.ServiceLocator
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SettingsViewModel : ViewModel(), okhttp3.Callback {
    private val prefs = ServiceLocator.getInstance().getSharedPreferences()
    private val networkService = ServiceLocator.getInstance().getNetworkClient()

    val isLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    val userInfo: MutableLiveData<PostmanUserInfo> = MutableLiveData()

    val requestPrefs: MutableLiveData<RequestPrefs> = MutableLiveData(prefs.getRequestPrefs())
    val webViewPrefs: MutableLiveData<WebViewPrefs> = MutableLiveData(prefs.getWebViewPrefs())

    val error: MutableLiveData<ErrorEvent> = MutableLiveData()

    private var apiKeyGuess: String = ""

    init {
        val savedUserInfo = prefs.getUserInfo()
        if (savedUserInfo != null) {
            userInfo.value = savedUserInfo
            isLoggedIn.value = true
        }

        requestPrefs.value = prefs.getRequestPrefs()
    }

    fun loginToPostman(apiKey: String) {
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
        prefs.deleteUserInfo()
        isLoggedIn.value = false
        userInfo.value = null
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
    }

    override fun onResponse(call: Call, response: Response) {
        val responseBody = response.body?.string()
        val gson = ServiceLocator.getInstance().getGson()

        if (response.code == 200) {
            val info = gson.fromJson(responseBody, PostmanGetMeInfo::class.java)
            userInfo.postValue(info.user)
            isLoggedIn.postValue(true)

            prefs.setApiKey(apiKeyGuess)
            prefs.setUserInfo(info.user)
        } else {
            error.postValue(ErrorEvent.AuthenticationError)
        }
    }
}