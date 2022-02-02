package com.hickar.restly.models

import com.hickar.restly.consts.RequestMethod

data class Request(
    var method: RequestMethod = RequestMethod.GET,
    var query: RequestQuery = RequestQuery(),
    var headers: List<RequestHeader> = mutableListOf(),
    var body: RequestBody = RequestBody(),
    var auth: RequestAuth? = null,
    var variables: List<CollectionVariable> = listOf(),
    var certificate: RequestCertificate? = null,
    var proxy: RequestProxyConfig? = null
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