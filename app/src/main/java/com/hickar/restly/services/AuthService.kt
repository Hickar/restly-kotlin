package com.hickar.restly.services

import com.google.gson.Gson
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import javax.inject.Inject

class AuthService @Inject constructor(
    private val gson: Gson,
    private val networkService: NetworkService
) {

    suspend fun loginToPostman(apiKey: String): PostmanUserInfo? {
        val request = Request(
            query = RequestQuery(POSTMAN_GETME_URL),
            method = RequestMethod.GET,
            headers = listOf(RequestHeader("X-Api-key", apiKey))
        )

        val response = networkService.sendRequest(request)
        when (response.code) {
            200 -> {
                val body = response.body?.string()
                if (body != null) {
                    return gson.fromJson(body, PostmanGetMeInfo::class.java)?.user
                }
            }
            else -> throw WrongApiKeyException("Invalid API key was provided")
        }

        return null
    }

    suspend fun loginToReslty(credentials: RestlyLoginCredentials): RestlyUserInfo? {
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

        val response = networkService.sendRequest(request)
        when (response.code) {
            200 -> {
                val body = response.body?.string()
                if (body != null) {
                    return gson.fromJson(body, RestlyUserInfo::class.java)
                } else {
                    throw EmptyAuthResponseBodyException("Server returned empty response")
                }
            }
            404, 409, 422 -> throw InvalidCredentialsException("Invalid Restly credentials were provided")
        }

        return null
    }

    suspend fun signUpInRestly(credentials: RestlySignupCredentials): String? {
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

        val response = networkService.sendRequest(request)
        when (response.code) {
            201 -> {
                val token = response.body?.string()
                if (token != null) {
                    return token
                } else {
                    throw EmptyAuthResponseBodyException("Server returned empty response")
                }
            }
            409, 422 -> {
                throw NotStrongPasswordException("Provided password doesn't meet strength requirements")
            }
        }

        return null
    }

    companion object {
        private const val POSTMAN_GETME_URL = "https://api.getpostman.com/me"
        private const val RESTLY_URL_DEV = "http://10.0.2.2:8080"
        private const val RESTLY_URL_PROD = "https://restly.com"
    }
}