package com.hickar.restly.models

data class Collection(
    var uid: String = DEFAULT,
    var id: String = DEFAULT,
    var name: String = "New Collection",
    var description: String = "",
    var owner: String = ""
) {
    companion object {
        const val DEFAULT = "0000-00000000-00000000-0000-000000-000000"
    }

    fun isDefault(): Boolean {
        return id == DEFAULT
    }
}