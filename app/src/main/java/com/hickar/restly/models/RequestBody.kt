package com.hickar.restly.models

data class RequestBody(
    var enabled: Boolean = false,
    var type: String = "",
    var rawText: String = "",
    var formData: List<RequestKeyValue> = listOf(),
    var multipartData: List<RequestKeyValue> = listOf(),
    var binaryData: RequestBodyBinary = RequestBodyBinary()
)