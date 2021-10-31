package com.hickar.restly.models

import com.hickar.restly.consts.RequestMethod

data class Request(
    var id: Long = 0,
    var method: RequestMethod = RequestMethod.GET,
    var name: String = "New Request",
    var url: String = "",
    var queryParams: List<RequestQueryParameter> = mutableListOf(),
    var headers: List<RequestHeader> = mutableListOf(),
    var body: RequestBody = RequestBody()
) {
    fun shouldHaveBody(): Boolean {
        return when (method) {
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.PATCH,
            RequestMethod.OPTIONS -> true
            else -> false
        }
    }
}