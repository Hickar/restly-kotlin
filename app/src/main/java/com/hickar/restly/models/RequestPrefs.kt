package com.hickar.restly.models

data class RequestPrefs(
    var sslVerificationEnabled: Boolean = true,
    var maxSize: Long = 0,
    var timeout: Long = 0
)