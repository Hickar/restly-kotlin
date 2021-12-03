package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hickar.restly.models.*
import com.hickar.restly.services.AuthService
import com.hickar.restly.services.NetworkService
import com.hickar.restly.services.SharedPreferencesHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import okhttp3.Call
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class SettingsViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val prefs: SharedPreferencesHelper,
    private val authService: AuthService
) : ViewModel(), AuthService.Delegate {
    @Inject
    lateinit var networkService: NetworkService

    @Inject
    lateinit var gson: Gson

    val isLoggedInRestly: MutableLiveData<Boolean> = MutableLiveData(false)
    val restlyUserInfo: MutableLiveData<RestlyUserInfo?> = MutableLiveData()

    val isLoggedInPostman: MutableLiveData<Boolean> = MutableLiveData(false)
    val postmanUserInfo: MutableLiveData<PostmanUserInfo?> = MutableLiveData()

    val requestPrefs: MutableLiveData<RequestPrefs> = MutableLiveData(prefs.getRequestPrefs())
    val webViewPrefs: MutableLiveData<WebViewPrefs> = MutableLiveData(prefs.getWebViewPrefs())

    val error: MutableLiveData<ErrorEvent> = MutableLiveData()

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
        viewModelScope.launch {
            authService.loginToReslty(
                RestlyLoginCredentials(username, password),
                this@SettingsViewModel
            )
        }
    }

    fun logoutFromRestly() {
        prefs.deleteRestlyUserInfo()
        isLoggedInRestly.value = false
        restlyUserInfo.value = null
    }

    fun signUpInRestly(email: String, username: String, password: String) {
        viewModelScope.launch {
            authService.signUpInRestly(
                RestlySignupCredentials(email, username, password),
                this@SettingsViewModel
            )
        }
    }

    fun loginToPostman(apiKey: String) {
        viewModelScope.launch {
            authService.loginToPostman(apiKey, this@SettingsViewModel)
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
            is InvalidCredentialsException -> ErrorEvent.InvalidCredentials
            is NotStrongPasswordException -> ErrorEvent.NotStrongPassword
            is UserExistsException -> ErrorEvent.UserExists
            is WrongApiKeyException -> ErrorEvent.PostmanAuthError
            else -> ErrorEvent.ConnectionUnexpected
        }
        error.postValue(newError)
    }

    override fun onRegistrationSuccess() {
    }

    override fun onPostmanLoginSuccess(userInfo: PostmanUserInfo) {
        postmanUserInfo.postValue(userInfo)
    }

    override fun onRestlyLoginSuccess(userInfo: RestlyUserInfo) {
        restlyUserInfo.postValue(userInfo)
    }

    @AssistedFactory
    interface Factory {
        fun build(handle: SavedStateHandle): SettingsViewModel
    }
}