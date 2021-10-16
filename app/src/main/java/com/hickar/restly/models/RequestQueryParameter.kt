package com.hickar.restly.models

class RequestQueryParameter (
    override var key: String = "",
    override var valueText: String = "",
    override var enabled: Boolean = true
) : RequestKeyValueData()