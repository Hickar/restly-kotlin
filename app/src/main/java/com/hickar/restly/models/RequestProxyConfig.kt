package com.hickar.restly.models

class RequestProxyConfig(
    var match: String = "",
    var host: String = "",
    var port: Int = 0,
    var tunnel: Boolean = false,
    var enabled: Boolean = true
)