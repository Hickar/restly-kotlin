package com.hickar.restly.models

import okhttp3.Headers

data class Response(
    val url: String,
    val headers: Headers,
    val contentType: String,
    val body: String,
    val code: Int,
    val roundTripTime: Long,
    val size: Long,
    val protocol: String,
    val isRedirected: Boolean
) {

    fun getGeneralParams(): List<Pair<String, String>> {
        return listOf(
            Pair("URL", url),
            Pair("Protocol", protocol),
            Pair("Response Code", code.toString()),
            Pair("Is Redirect", isRedirected.toString()),
            Pair("Response Content-Type", contentType)
        )
    }

    fun getHeaders(): List<Pair<String, String>> {
        return headers.toList()
    }
}