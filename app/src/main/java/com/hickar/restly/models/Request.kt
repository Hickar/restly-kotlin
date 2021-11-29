package com.hickar.restly.models

import com.hickar.restly.consts.RequestMethod
import java.util.*

data class Request(
    var id: String = UUID.randomUUID().toString(),
    var method: RequestMethod = RequestMethod.GET,
    var name: String = "New Request",
    var query: RequestQuery = RequestQuery(),
    var headers: List<RequestHeader> = mutableListOf(),
    var body: RequestBody = RequestBody(),
    var parentId: String = Collection.DEFAULT
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