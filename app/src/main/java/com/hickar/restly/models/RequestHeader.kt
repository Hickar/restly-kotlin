package com.hickar.restly.models

class RequestHeader (
    override var key: String = "",
    override var valueText: String = "",
    override var enabled: Boolean = true
) : RequestKeyValueData()