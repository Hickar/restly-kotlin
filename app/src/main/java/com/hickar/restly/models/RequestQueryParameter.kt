package com.hickar.restly.models

import java.util.*

data class RequestQueryParameter (
    override var key: String = "",
    override var value: String = "",
    override var enabled: Boolean = true,
    override var uid: String = UUID.randomUUID().toString()
) : RequestKeyValueData() {

    override fun toString(): String {
        val valuePart = if (value.isNotBlank()) {
            "=$value"
        } else {
            ""
        }

        return "${key}${valuePart}"
    }

    override fun equals(other: Any?): Boolean {
        return other is RequestQueryParameter && uid == other.uid && key == other.key && value == other.value
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + uid.hashCode()
        return result
    }
}