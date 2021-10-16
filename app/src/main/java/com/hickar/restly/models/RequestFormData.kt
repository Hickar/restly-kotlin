package com.hickar.restly.models

data class RequestFormData (
    override var key: String = "",
    override var valueText: String = "",
    override var enabled: Boolean = true
) : RequestKeyValueData()