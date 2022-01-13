package com.hickar.restly.models

import java.util.*

class RequestDirectory(
    var id: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String? = "",
    var requests: MutableList<Request> = mutableListOf(),
    var groups: MutableList<RequestDirectory> = mutableListOf(),
    override var parentId: String? = null
) : RequestGroup() {
    override fun equals(other: Any?): Boolean {
        return other is RequestDirectory
                && other.id == id
                && other.name == name
                && other.description == description
                && other.requests == requests
                && other.groups == groups
                && other.parentId == parentId
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + requests.hashCode()
        result = 31 * result + groups.hashCode()
        result = 31 * result + (parentId?.hashCode() ?: 0)
        return result
    }

    companion object {
        const val DEFAULT = "0000-00000000-00000000-0000-000000-000000"
    }

    fun isDefault(): Boolean {
        return id == DEFAULT
    }

    fun isRoot(): Boolean {
        return parentId == null
    }
}