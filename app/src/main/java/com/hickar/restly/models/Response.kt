package com.hickar.restly.models

data class Response(
    val url: String,
    val headers: List<Pair<String, String>>,
    val contentType: String,
    val body: String,
    val code: Int
) {
}