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
            query = RequestQuery("https://api.getpostman.com/me"),
            method = RequestMethod.GET,
            headers = listOf(RequestHeader("X-Api-key", apiKey))
        )

        networkService.sendRequest(request, object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                delegate.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                if (body != null) {
                    when (response.code) {
                        200 -> {
                            val userInfo = gson.fromJson(body, PostmanGetMeInfo::class.java)?.user
                            if (userInfo != null) {
                                prefs.setPostmanApiKey(apiKey)
                                prefs.setPostmanUserInfo(userInfo)
                                delegate.onPostmanLoginSuccess(userInfo)
                            }
                        }
                        else -> {
                            delegate.onFailure(call, WrongApiKeyException())
                            return
                        }
                    }
                }
            }
        })
    }

    suspend fun loginToReslty(credentials: RestlyLoginCredentials, delegate: Delegate) {
        val request = Request(
            query = RequestQuery("http://10.0.2.2:8080/api/authorize"),
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
                val body = response.body?.string()

                if (body != null) {
                    when (response.code) {
                        200 -> {
                            prefs.setRestlyJwt(body)

                            val userInfo = gson.fromJson(body, RestlyUserInfo::class.java)
                            delegate.onRestlyLoginSuccess(userInfo)
                        }
                        409 -> delegate.onFailure(call, InvalidCredentialsException())
                    }
                } else {
                    delegate.onFailure(call, EmptyAuthResponseBodyException())
                }
            }
        })
    }

    suspend fun signUpInRestly(credentials: RestlySignupCredentials, delegate: Delegate) {
        val request = Request(
            query = RequestQuery("http://10.0.2.2:8080/api/user"),
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
                val body = response.body?.string()

                if (body != null) {
                    when (response.code) {
                        201 -> {
                            prefs.setRestlyJwt(body)
                            delegate.onRegistrationSuccess()
                        }
                        409 -> {
                            delegate.onFailure(call, NotStrongPasswordException())
                            return
                        }
                    }
                } else {
                    delegate.onFailure(call, EmptyAuthResponseBodyException())
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
}