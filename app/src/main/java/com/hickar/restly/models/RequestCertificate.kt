package com.hickar.restly.models

class RequestCertificate(
    var name: String,
    var matches: List<String>,
    var srcPath: String,
    var certPath: String,
    var passphrase: String
)