package com.hickar.restly.models

import java.util.*

class RequestDirectory(
    var id: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String?,
    override var parentId: String?
) : RequestGroup()