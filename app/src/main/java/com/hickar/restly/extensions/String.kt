package com.hickar.restly.extensions

import android.text.Editable

fun String.toEditable(): Editable {
    return Editable.Factory().newEditable(this)
}