package com.hickar.restly.models

data class RequestFile(
    val name: String,
    val uri: String,
    val size: Int
) {
    fun isEmpty(): Boolean {
        return uri.isEmpty() || name.isEmpty()
    }
}