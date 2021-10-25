package com.hickar.restly.models

import java.io.File

data class ResponseBody(
    val contentType: String,
    val size: Long,
    val rawData: String?,
    val file: File?
) {
    fun isRawViewSupported(): Boolean {
        return rawData != null && (
                contentType.contains("text") ||
                contentType.contains("json") ||
                contentType.contains("html")
        )
    }
}