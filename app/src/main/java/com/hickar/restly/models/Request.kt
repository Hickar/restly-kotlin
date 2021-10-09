package com.hickar.restly.models

data class Request(
    var id: Long = 0,
    var method: String = "GET",
    var name: String = "New Request",
    var url: String = "",
    var queryParams: List<RequestQueryParameter> = mutableListOf(),
    var headers: List<RequestHeader> = mutableListOf(),
    var body: RequestBody = RequestBody()
)