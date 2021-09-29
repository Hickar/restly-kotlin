package com.hickar.restly.models

data class RequestHeader(
    var key: String,
    var value: String,
    var enabled: Boolean
)