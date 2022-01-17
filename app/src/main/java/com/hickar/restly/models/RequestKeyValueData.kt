package com.hickar.restly.models

abstract class RequestKeyValueData {
    abstract var key: String
    abstract var value: String
    abstract var enabled: Boolean
    abstract var uid: String
    abstract var description: String?

    override fun equals(other: Any?): Boolean {
        return other is RequestKeyValueData
                && key == other.key
                && value == other.value
                && enabled == other.enabled
                && uid == other.uid
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + uid.hashCode()
        return result
    }
}