package com.hickar.restly.models

import com.hickar.restly.extensions.toResponseTime
import okhttp3.Headers
import java.text.SimpleDateFormat
import java.util.*

data class Response(
    val url: String,
    val headers: Headers,
    val code: Int,
    val receivedAt: Date,
    val sentAt: Date,
    val roundTripTime: Long,
    val protocol: String,
    val isRedirected: Boolean,
    val body: ResponseBody
) {

    fun getGeneralParams(): List<Pair<String, String>> {
        val timeFormat = SimpleDateFormat("hh:mm:ss MM-dd-yy", Locale.getDefault())

        return listOf(
            Pair("URL", url),
            Pair("Protocol", protocol),
            Pair("Response Code", code.toString()),
            Pair("Is Redirect", isRedirected.toString()),
            Pair("Content-Type", body.contentType),
            Pair("Time sent", timeFormat.format(sentAt)),
            Pair("Time received", timeFormat.format(receivedAt)),
            Pair("Round trip time", roundTripTime.toResponseTime())
        )
    }

    fun getHeaders(): List<Pair<String, String>> {
        return headers.toList()
    }
}