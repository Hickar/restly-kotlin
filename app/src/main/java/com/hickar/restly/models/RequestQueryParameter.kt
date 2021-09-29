package com.hickar.restly.models

data class RequestQueryParameter(
    override var key: String = "",
    override var value: String = "",
    override var enabled: Boolean = false
) : RequestKeyValue(key, value, enabled)