package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.models.*
import com.hickar.restly.repository.room.CollectionRepository
import com.hickar.restly.services.AuthService
import com.hickar.restly.services.SharedPreferencesHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class SettingsViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val prefs: SharedPreferencesHelper,
    private val collectionRepository: CollectionRepository,
    private val authService: AuthService
) : ViewModel() {
    private var _postmanUserInfo = MutableStateFlow<PostmanUserInfo?>(null)
    val postmanUserInfo: StateFlow<PostmanUserInfo?> get() = _postmanUserInfo

    private var _requestPrefs = MutableStateFlow(RequestPrefs())
    val requestPrefs: StateFlow<RequestPrefs> get() = _requestPrefs

    private var _webViewPrefs = MutableStateFlow(WebViewPrefs())
    val webViewPrefs: StateFlow<WebViewPrefs> get() = _webViewPrefs

    val error: MutableLiveData<ErrorEvent?> = MutableLiveData()

    init {
        viewModelScope.launch {
            prefs.getRequestPrefs().collect {
                _requestPrefs.value = it
            }

            prefs.getWebViewPrefs().collect {
                _webViewPrefs.value = it
            }

            prefs.getPostmanUserInfo().collect {
                _postmanUserInfo.value = it
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
                }
            } catch (e: IOException) {
                error.postValue(getErrorEvent(e))
            }
        }
    }

    fun logoutFromPostman(shouldDeleteRemoteCollections: Boolean = false) {
        viewModelScope.launch {
            prefs.deletePostmanUserInfo()
            prefs.deletePostmanApiKey()
            if (shouldDeleteRemoteCollections) {
                collectionRepository.deleteRemoteCollections()
            }
        }
    }

    fun setRequestSslVerificationEnabled(enabled: Boolean) = viewModelScope.launch {
        _requestPrefs.value.sslVerificationEnabled = enabled
        prefs.setRequestPrefs(_requestPrefs.value)
    }

    fun setRequestMaxSize(maxSize: Long) = viewModelScope.launch {
        _requestPrefs.value.maxSize = maxSize
        prefs.setRequestPrefs(_requestPrefs.value)
    }

    fun setRequestTimeout(timeout: Long) = viewModelScope.launch {
        _requestPrefs.value.timeout = timeout
        prefs.setRequestPrefs(_requestPrefs.value)
    }

    fun setWebViewJavascriptEnabled(enabled: Boolean) = viewModelScope.launch {
        _webViewPrefs.value.javascriptEnabled = enabled
        prefs.setWebViewPrefs(_webViewPrefs.value)
    }

    fun setWebViewTextSize(textSize: Int) = viewModelScope.launch {
        _webViewPrefs.value.textSize = textSize
        prefs.setWebViewPrefs(_webViewPrefs.value)
    }

    private fun getErrorEvent(e: IOException): ErrorEvent {
        return when (e) {
            is SocketTimeoutException -> ErrorEvent.ConnectionTimeout
            is UnknownHostException -> ErrorEvent.UnknownHostError
            is NetworkUnavailableException -> ErrorEvent.NoInternetConnectionError
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