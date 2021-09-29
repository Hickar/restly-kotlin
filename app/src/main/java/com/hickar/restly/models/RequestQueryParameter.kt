package com.hickar.restly.models

data class RequestQueryParameter(
    var key: String = "",
    var value: String = "",
    var enabled: Boolean = false
)