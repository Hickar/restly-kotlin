package com.hickar.restly.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.hickar.restly.repository.models.CollectionRemoteDTO
import java.lang.reflect.Type

class CollectionJsonDeserializer : JsonDeserializer<CollectionRemoteDTO> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CollectionRemoteDTO {
        val jsonObject = json?.asJsonObject ?: throw JsonParseException("Json element is null")

        val collection = CollectionRemoteDTO()

        jsonObject.get("info").asJsonObject.let {
            collection.id = it.get("_postman_id").asString
            if (it.has("description")) collection.name = it.get("name").asString
        }

        val root = jsonObject.get("item").asJsonArray
        for (element in root) {

        }

        return CollectionRemoteDTO()
    }
}