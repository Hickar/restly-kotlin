package com.hickar.restly.models

data class RestlyLoginCredentials(
    var email: String,
    var password: String
)

data class RestlySignupCredentials(
    var email: String,
    var name: String,
    var password: String
)