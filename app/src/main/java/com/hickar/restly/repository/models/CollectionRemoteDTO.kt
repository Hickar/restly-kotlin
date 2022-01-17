package com.hickar.restly.repository.models

import com.hickar.restly.models.RequestDirectory

data class CollectionRemoteDTO(
    var id: String = "",
    var name: String = "",
    val description: String = "",
    val owner: String = "",
    val root: RequestDirectory? = null
)