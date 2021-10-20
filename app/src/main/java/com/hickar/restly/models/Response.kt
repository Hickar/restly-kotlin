package com.hickar.restly.models

import okhttp3.Headers

data class Response(
    val url: String,
    val headers: List<Pair<String, String>>,
    val contentType: String,
    val body: String,
    val code: Int
) {
}