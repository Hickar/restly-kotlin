package com.hickar.restly.models

import java.io.File

data class ResponseBody(
    val contentType: String,
    val size: Long,
    val rawData: String?,
    val file: File?
)