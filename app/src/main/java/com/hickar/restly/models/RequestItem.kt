package com.hickar.restly.models

import java.util.*

class RequestItem(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "New Request",
    var description: String = "",
    var request: Request = Request(),
    var parentId: String,
    var variables: List<CollectionVariable> = listOf(),
    var events: List<CollectionEvent> = listOf()
) {
    override fun equals(other: Any?): Boolean {
        return other is RequestItem && other.id == id && other.request == request
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + parentId.hashCode()
        return result
    }
}