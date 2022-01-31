package com.hickar.restly.viewModel

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

    val error: MutableStateFlow<ErrorEvent?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            prefs.getRequestPrefs().collect {
                _requestPrefs.value = it
            }
        }

        viewModelScope.launch {
            prefs.getWebViewPrefs().collect {
                _webViewPrefs.value = it
            }
        }

        viewModelScope.launch {
            prefs.getPostmanUserInfo().collect {
                _postmanUserInfo.value = it
            }
        }
    }


    fun loginToPostman(apiKey: String) = viewModelScope.launch {
        try {
            val userInfo = authService.loginToPostman(apiKey)
            if (userInfo != null) {
                prefs.setPostmanApiKey(apiKey)
                prefs.setPostmanUserInfo(userInfo)
            }
        } catch (e: IOException) {
            error.value = getErrorEvent(e)
        }
    }

    fun logoutFromPostman(shouldDeleteRemoteCollections: Boolean = false) = viewModelScope.launch {
        prefs.deletePostmanUserInfo()
        prefs.deletePostmanApiKey()
        if (shouldDeleteRemoteCollections) {
            collectionRepository.deleteRemoteCollections()
        }
    }

    fun setRequestSslVerificationEnabled(enabled: Boolean) = viewModelScope.launch {
        prefs.setRequestPrefs(_requestPrefs.value.copy(sslVerificationEnabled = enabled))
    }

    fun setRequestMaxSize(maxSize: Long) = viewModelScope.launch {
        prefs.setRequestPrefs(_requestPrefs.value.copy(maxSize = maxSize))
    }

    fun setRequestTimeout(timeout: Long) = viewModelScope.launch {
        prefs.setRequestPrefs(_requestPrefs.value.copy(timeout = timeout))
    }

    fun setWebViewJavascriptEnabled(enabled: Boolean) = viewModelScope.launch {
        prefs.setWebViewPrefs(_webViewPrefs.value.copy(javascriptEnabled = enabled))
    }

    fun setWebViewTextSize(textSize: Int) = viewModelScope.launch {
        prefs.setWebViewPrefs(_webViewPrefs.value.copy(textSize = textSize))
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