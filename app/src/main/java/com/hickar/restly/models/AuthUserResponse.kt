package com.hickar.restly.models

data class AuthUserResponse(
    var id: String,
    var username: String,
    var email: String,
    var token: String
)