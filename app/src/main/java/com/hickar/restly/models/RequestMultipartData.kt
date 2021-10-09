package com.hickar.restly.models

data class RequestMultipartData(
    override var key: String = "",
    override var value: String = "",
    override var enabled: Boolean = true
) : RequestKeyValueData()