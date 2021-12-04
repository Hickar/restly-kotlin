package com.hickar.restly.services

import com.google.gson.Gson
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthService @Inject constructor(
    private val gson: Gson,
    private val prefs: SharedPreferencesHelper,
    private val networkService: NetworkService
) {

    suspend fun loginToPostman(apiKey: String, delegate: Delegate) {
        val request = Request(
            query = RequestQuery(POSTMAN_GETME_URL),
            method = RequestMethod.GET,
            headers = listOf(RequestHeader("X-Api-key", apiKey))
        )

        networkService.sendRequest(request, object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                delegate.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> {
                        val body = response.body?.string()
                        if (body != null) {
                            val userInfo = gson.fromJson(body, PostmanGetMeInfo::class.java)?.user
                            if (userInfo != null) {
                                prefs.setPostmanApiKey(apiKey)
                                prefs.setPostmanUserInfo(userInfo)
                                delegate.onPostmanLoginSuccess(userInfo)
                            }
                        } else {
                            delegate.onFailure(call, WrongApiKeyException())
                        }
                    }
                }
            }
        })
    }

    suspend fun loginToReslty(credentials: RestlyLoginCredentials, delegate: Delegate) {
        val request = Request(
            query = RequestQuery("$RESTLY_URL_DEV/api/authorize"),
            method = RequestMethod.POST,
            body = RequestBody(
                enabled = true,
                type = BodyType.RAW,
                rawData = RequestRawData(
                    gson.toJson(credentials),
                    "application/json"
                )
            )
        )

        networkService.sendRequest(request, object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                delegate.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> {
                        val body = response.body?.string()
                        if (body != null) {
                            val userInfo = gson.fromJson(body, RestlyUserInfo::class.java)

                            prefs.setRestlyUserInfo(userInfo)
                            prefs.setRestlyJwt(userInfo.token)
                            delegate.onRestlyLoginSuccess(userInfo)
                        } else {
                            delegate.onFailure(call, EmptyAuthResponseBodyException())
                        }
                    }
                    404, 409, 422 -> delegate.onFailure(call, InvalidCredentialsException())
                }
            }
        })
    }

    suspend fun signUpInRestly(credentials: RestlySignupCredentials, delegate: Delegate) {
        val request = Request(
            query = RequestQuery("$RESTLY_URL_DEV/api/user"),
            method = RequestMethod.POST,
            body = RequestBody(
                enabled = true,
                type = BodyType.RAW,
                rawData = RequestRawData(
                    gson.toJson(credentials),
                    "application/json"
                )
            )
        )

        networkService.sendRequest(request, object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                delegate.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    201 -> {
                        val body = response.body?.string()
                        if (body != null) {
                            prefs.setRestlyJwt(body)
                            delegate.onRegistrationSuccess()
                        } else {
                            delegate.onFailure(call, EmptyAuthResponseBodyException())
                        }
                    }
                    409, 422 -> {
                        delegate.onFailure(call, NotStrongPasswordException())
                        return
                    }
                }
            }
        })
    }

    interface Delegate {
        fun onRegistrationSuccess()

        fun onRestlyLoginSuccess(userInfo: RestlyUserInfo)

        fun onPostmanLoginSuccess(userInfo: PostmanUserInfo)

        fun onFailure(call: Call, e: IOException)
    }

    companion object {
        private const val POSTMAN_GETME_URL = "https://api.getpostman.com/me"
        private const val RESTLY_URL_DEV = "http://10.0.2.2:8080"
        private const val RESTLY_URL_PROD = "https://restly.com"
    }
}