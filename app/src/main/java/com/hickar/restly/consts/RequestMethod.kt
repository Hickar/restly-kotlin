package com.hickar.restly.consts

import com.google.gson.annotations.SerializedName

enum class RequestMethod(val method: String) {
    @SerializedName("get")
    GET("GET"),
    @SerializedName("post")
    POST("POST"),
    @SerializedName("put")
    PUT("PUT"),
    @SerializedName("path")
    PATCH("PATCH"),
    @SerializedName("head")
    HEAD("HEAD"),
    @SerializedName("options")
    OPTIONS("OPTIONS"),
    @SerializedName("delete")
    DELETE("DELETE")
}