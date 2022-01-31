package com.hickar.restly.services

import com.google.gson.Gson
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
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

    companion object {
        private const val POSTMAN_GETME_URL = "https://api.getpostman.com/me"
    }
}