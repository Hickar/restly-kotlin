package com.hickar.restly.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.hickar.restly.repository.dao.CollectionInfo
import java.lang.reflect.Type

class CollectionInfoJsonDesetializer : JsonDeserializer<List<CollectionInfo>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<CollectionInfo>? {
        val collectionInfoList: MutableList<CollectionInfo> = mutableListOf()
        val jsonObject = json?.asJsonObject ?: return null

        if (jsonObject.has("collections")) {
            val entries = jsonObject.get("collections").asJsonArray
            for (entry in entries) {
                val entryObject = entry.asJsonObject ?: continue
                val collectionInfoEntry = CollectionInfo(
                    id = entryObject.get("id").asString,
                    name = entryObject.get("name").asString,
                    owner = entryObject.get("owner").asString,
                    uid = entryObject.get("uid").asString
                )

                collectionInfoList.add(collectionInfoEntry)
            }
        }

        return if (collectionInfoList.size > 0) {
            collectionInfoList
        } else {
            null
        }
    }
}