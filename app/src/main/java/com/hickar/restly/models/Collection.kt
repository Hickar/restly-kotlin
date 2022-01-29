package com.hickar.restly.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Collection(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "New Collection",
    var description: String = "",
    var owner: String = "",
    override var parentId: String? = null,
    var origin: CollectionOrigin = CollectionOrigin.LOCAL
) : RequestGroup() {
    companion object {
        const val DEFAULT_ID = "0000-00000000-00000000-0000-000000-000000"

        fun getDefault(): Collection {
            return Collection(id = DEFAULT_ID)
        }
    }

    fun isDefault(): Boolean {
        return id == DEFAULT_ID
    }
}

enum class CollectionOrigin(val origin: String) {
    @SerializedName("Postman")
    LOCAL("local"),

    @SerializedName("Postman")
    POSTMAN("postman")
}