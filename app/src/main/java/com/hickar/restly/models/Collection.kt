package com.hickar.restly.models

import java.util.*

data class Collection(
    var uid: String = UUID.randomUUID().toString(),
    var id: String = UUID.randomUUID().toString(),
    var name: String = "New Collection",
    var description: String = "",
    var owner: String = ""
) {
    companion object {
        const val DEFAULT = "0000-00000000-00000000-0000-000000-000000"
    }
}