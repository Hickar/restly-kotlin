package com.hickar.restly.repository.models

import com.hickar.restly.models.Request

data class CollectionRemoteDTO(
    val id: String,
    val name: String,
    val description: String,
    val owner: String,
    val items: List<Request>
)