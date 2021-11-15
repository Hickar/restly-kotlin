package com.hickar.restly.models

data class PostmanUserInfo(
    var id: Int = 0,
    var username: String = "",
    var email: String = "",
    var fullName: String = "",
    var avatar: String? = null,
    var isPublic: Boolean = false
)

data class PostmanOperation(
    var name: String = "",
    var limit: Int = 0,
    var usage: Int = 0,
    var overage: Int = 0
)

data class PostmanGetMeInfo(
    var user: PostmanUserInfo = PostmanUserInfo(),
    var operations: List<PostmanOperation> = listOf()
)