package com.hickar.restly.repository.models

import com.hickar.restly.models.CollectionEvent
import com.hickar.restly.models.CollectionVariable
import com.hickar.restly.models.RequestDirectory

data class CollectionRemoteDTO(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var owner: String = "",
    var root: RequestDirectory? = null,
    var variables: List<CollectionVariable> = listOf(),
    var events: List<CollectionEvent> = listOf()
)