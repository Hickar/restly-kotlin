package com.hickar.restly.models

data class Request(
    var id: Long = 0,
    var method: String = "GET",
    var name: String = "New Request",
    var url: String = "",
    var queryParams: MutableList<RequestKeyValueParameter> = mutableListOf(),
    var headers: MutableList<RequestKeyValueParameter> = mutableListOf(),
    var body: RequestBody = RequestBody()
)