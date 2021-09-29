package com.hickar.restly.models

data class RequestHeader(
    override var key: String = "",
    override var value: String = "",
    override var enabled: Boolean = false
) : RequestKeyValue(key, value, enabled)