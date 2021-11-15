package com.hickar.restly.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.PostmanGetMeInfo
import com.hickar.restly.models.PostmanUserInfo
import com.hickar.restly.models.RequestHeader
import com.hickar.restly.services.ServiceLocator
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

class SettingsViewModel : ViewModel(), okhttp3.Callback {
    private val prefs = ServiceLocator.getInstance().getSharedPreferences()
    private val networkService = ServiceLocator.getInstance().getNetworkClient()

    val isLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    val userInfo: MutableLiveData<PostmanUserInfo> = MutableLiveData()

    private var apiKeyGuess: String = ""

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

    override fun onFailure(call: Call, e: IOException) {
        TODO("Not implemented yet")
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
            TODO("Error handling is not implemented yet")
        }
    }
}