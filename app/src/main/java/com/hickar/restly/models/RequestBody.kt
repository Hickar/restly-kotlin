package com.hickar.restly.models

import com.google.gson.annotations.SerializedName

enum class BodyType(val type: String) {
    @SerializedName("urlencoded")
    FORMDATA("URLEncoded"),

    @SerializedName("multipart")
    MULTIPART("FormData"),

    @SerializedName("raw")
    RAW("Raw"),

    @SerializedName("binary")
    BINARY("Binary"),

    @SerializedName("none")
    NONE("None")
}

data class RequestBody(
    var enabled: Boolean = false,
    var type: BodyType = BodyType.FORMDATA,
    var rawData: RequestRawData = RequestRawData(),
    var formData: List<RequestFormData> = listOf(),
    var multipartData: List<RequestMultipartData> = listOf(),
    var binaryData: RequestBinaryData = RequestBinaryData()
)