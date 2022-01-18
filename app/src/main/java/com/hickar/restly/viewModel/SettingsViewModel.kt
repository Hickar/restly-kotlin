package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hickar.restly.models.*
import com.hickar.restly.repository.room.CollectionRepository
import com.hickar.restly.services.AuthService
import com.hickar.restly.services.NetworkService
import com.hickar.restly.services.SharedPreferencesHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class SettingsViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val prefs: SharedPreferencesHelper,
    private val collectionRepository: CollectionRepository,
    private val authService: AuthService
) : ViewModel() {
    @Inject
    lateinit var networkService: NetworkService

    @Inject
    lateinit var gson: Gson

    val successfulRegistration: MutableLiveData<Boolean> = MutableLiveData(false)

    val isLoggedInRestly: MutableLiveData<Boolean> = MutableLiveData(false)
    val restlyUserInfo: MutableLiveData<RestlyUserInfo?> = MutableLiveData()

    val isLoggedInPostman: MutableLiveData<Boolean> = MutableLiveData(false)
    val postmanUserInfo: MutableLiveData<PostmanUserInfo?> = MutableLiveData()

    val requestPrefs: MutableLiveData<RequestPrefs> = MutableLiveData(prefs.getRequestPrefs())
    val webViewPrefs: MutableLiveData<WebViewPrefs> = MutableLiveData(prefs.getWebViewPrefs())

    val error: MutableLiveData<ErrorEvent?> = MutableLiveData()

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
            try {
                val credentials = RestlyLoginCredentials(username, password)
                val userInfo = authService.loginToReslty(credentials)
                if (userInfo != null) {
                    prefs.setRestlyUserInfo(userInfo)
                    prefs.setRestlyJwt(userInfo.token)
                }
            } catch (e: IOException) {
                error.postValue(getErrorEvent(e))
            }
        }
    }

    fun logoutFromRestly() {
        prefs.deleteRestlyUserInfo()
        isLoggedInRestly.value = false
        restlyUserInfo.value = null
    }

    fun signUpInRestly(email: String, username: String, password: String) {
        viewModelScope.launch {
            try {
                val token = authService.signUpInRestly(RestlySignupCredentials(email, username, password))
                if (token != null) prefs.setRestlyJwt(token)
            } catch (e: IOException) {
                error.postValue(getErrorEvent(e))
            }
        }
    }

    fun loginToPostman(apiKey: String) {
        viewModelScope.launch {
            try {
                val userInfo = authService.loginToPostman(apiKey)
                if (userInfo != null) {
                    prefs.setPostmanApiKey(apiKey)
                    prefs.setPostmanUserInfo(userInfo)
                    postmanUserInfo.postValue(userInfo)
                    isLoggedInPostman.postValue(true)
                }
            } catch (e: IOException) {
                error.postValue(getErrorEvent(e))
            }
        }
    }

    fun logoutFromPostman(shouldDeleteRemoteCollections: Boolean = false) {
        prefs.deletePostmanUserInfo()
        prefs.deletePostmanApiKey()

        isLoggedInPostman.value = false
        postmanUserInfo.value = null

        if (shouldDeleteRemoteCollections) {
            viewModelScope.launch { collectionRepository.deleteRemoteCollections() }
        }
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

    private fun getErrorEvent(e: IOException): ErrorEvent {
        return when (e) {
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
    }

    @AssistedFactory
    interface Factory {
        fun build(handle: SavedStateHandle): SettingsViewModel
    }
}