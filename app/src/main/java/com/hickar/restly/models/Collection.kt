package com.hickar.restly.models

data class Collection(
    var id: String = DEFAULT,
    var name: String = "New Collection",
    var description: String = "",
    var owner: String = "",
    override var parentId: String? = null
): RequestGroup() {
    companion object {
        const val DEFAULT = "0000-00000000-00000000-0000-000000-000000"
    }

    fun isDefault(): Boolean {
        return id == DEFAULT
    }
}