package com.hickar.restly.models

import java.util.*

data class RequestQueryParameter (
    override var key: String = "",
    override var valueText: String = "",
    override var enabled: Boolean = true
) : RequestKeyValueData() {
    private val uid: String = UUID.randomUUID().toString()

    override fun equals(other: Any?): Boolean {
        return other is RequestQueryParameter && uid == other.uid && key == other.key && valueText == other.valueText
    }

    override fun toString(): String {
        val valuePart = if (valueText.isNotBlank()) {
            "=$valueText"
        } else {
            ""
        }

        return "${key}${valuePart}"
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + valueText.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + uid.hashCode()
        return result
    }
}