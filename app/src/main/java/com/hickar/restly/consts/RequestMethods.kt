package com.hickar.restly.consts

enum class RequestMethods(val method: String) {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    DELETE("DELETE")
}