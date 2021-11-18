package com.hickar.restly.extensions

import android.text.Editable
import kotlin.math.min

fun String.toEditable(): Editable {
    return Editable.Factory().newEditable(this)
}

fun String.indexOfDiff(other: String): Int {
    val minLength = min(this.length, other.length)
    for (i in 0 until minLength) {
        if (this[i] != other[i]) return i
    }

    return if (this.length != other.length) {
        minLength
    } else {
        -1
    }
}

fun String.toLongSafely(): Long {
    return try {
        this.toLong()
    } catch (e: NumberFormatException) {
        if (this < Long.MIN_VALUE.toString()) {
            Long.MIN_VALUE
        } else {
            Long.MAX_VALUE
        }
    }
}