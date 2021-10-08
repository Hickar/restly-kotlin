package com.hickar.restly.models


data class RequestBody(
    var enabled: Boolean = false,
    var typeSelected: String = FORMDATA,
    var type: String = "",
    var rawText: String = "",
    var formData: List<RequestKeyValueParameter> = listOf(),
    var multipartData: List<RequestKeyValueParameter> = listOf(),
    var binaryData: RequestBodyBinary = RequestBodyBinary()
) {
    companion object {
        const val FORMDATA = "formdata"
        const val MULTIPART = "multipart"
        const val RAW = "raw"
        const val BINARY = "binary"
    }
}