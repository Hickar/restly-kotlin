package com.hickar.restly.models

open class RequestKeyValue(
    open var key: String = "",
    open var value: String = "",
    open var enabled: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        return other is RequestKeyValue &&
                other.key == key &&
                other.value == value
    }
}