package com.hickar.restly.models

import java.util.*

class RequestQueryParameter (
    override var key: String = "",
    override var valueText: String = "",
    override var enabled: Boolean = true
) : RequestKeyValueData() {
    private val uid: String = UUID.randomUUID().toString()

    override fun equals(other: Any?): Boolean {
        return other is RequestQueryParameter && uid == other.uid && key == other.key && valueText == other.valueText
    }
}