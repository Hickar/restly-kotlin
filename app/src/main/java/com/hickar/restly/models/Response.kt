package com.hickar.restly.models

import okhttp3.Headers

data class Response(
    val url: String,
    val headers: Headers,
    val contentType: String,
    val body: String,
    val code: Int,
    val roundTripTime: Long,
    val size: Long
) {
}