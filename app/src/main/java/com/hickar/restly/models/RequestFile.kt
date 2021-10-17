package com.hickar.restly.models

data class RequestFile(
    val name: String,
    val path: String,
    val uri: String,
    val mimeType: String,
    val size: Int,
) {
    fun isEmpty(): Boolean {
        return uri.isEmpty() || name.isEmpty()
    }
}