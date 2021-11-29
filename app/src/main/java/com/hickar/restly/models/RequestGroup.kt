package com.hickar.restly.models

abstract class RequestGroup {
    abstract var parentId: String?

    fun isCollection(): Boolean {
        return parentId == null
    }
}