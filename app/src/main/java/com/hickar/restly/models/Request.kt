package com.hickar.restly.models

import com.hickar.restly.consts.RequestMethod
import java.util.*

data class Request(
    var id: Long = 0,
    var method: RequestMethod = RequestMethod.GET,
    var name: String = "New Request",
    var query: RequestQuery = RequestQuery(),
    var headers: List<RequestHeader> = mutableListOf(),
    var body: RequestBody = RequestBody(),
    var collectionId: String = UUID.randomUUID().toString()
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