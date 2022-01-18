package com.hickar.restly.utils

import com.google.gson.*
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.*
import com.hickar.restly.repository.models.CollectionRemoteDTO
import java.lang.reflect.Type

class CollectionJsonDeserializer : JsonDeserializer<CollectionRemoteDTO> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CollectionRemoteDTO? {
        val jsonObject = json?.asJsonObject ?: return null
        val collectionObject = jsonObject.get("collection").asJsonObject

        val collection = CollectionRemoteDTO()

        collectionObject.get("info").asJsonObject.let {
            collection.id = it.get("_postman_id").asString
            collection.name = it.get("name").asString
            if (it.has("description")) collection.description = it.get("description").asString

            collection.root = RequestDirectory(
                id = collection.id,
                name = collection.name,
                description = collection.description,
                parentId = null
            )
        }

        val rootObject = collectionObject.get("item").asJsonArray
        buildRequestGroup(rootObject, collection.root!!)

        return collection
    }

    private fun buildRequestGroup(itemList: JsonArray, requestGroup: RequestDirectory) {
        if (itemList.isEmpty) return

        for (item in itemList) {
            val itemObject = item.asJsonObject

            itemObject.run {
                if (has("item")) {
                    val description = if (has("description")) {
                        get("description").asString
                    } else {
                        ""
                    }

                    val newRequestGroup = RequestDirectory(
                        id = get("id").asString,
                        name = get("name").asString,
                        description = description,
                        parentId = requestGroup.id
                    )

                    val requestGroupsObject = get("item").asJsonArray
                    buildRequestGroup(requestGroupsObject, newRequestGroup)

                    requestGroup.subgroups.add(newRequestGroup)
                }

                if (has("request")) {
                    val newRequestItem = getRequestItem(itemObject, requestGroup)
                    requestGroup.requests.add(newRequestItem)
                }
            }
        }
    }

    private fun getRequestItem(requestItemObject: JsonObject, requestGroup: RequestDirectory): RequestItem {
        requestItemObject.run {
            if (!has("request")) {
                throw JsonParseException("Invalid requestItem object: should contain \"request\" key")
            }

            val itemDescription = if (has("description")) {
                get("description").asString
            } else {
                ""
            }

            val requestObject = get("request").asJsonObject

            var query = RequestQuery()
            if (requestObject.has("url")) {
                if (!requestObject.get("url").isJsonNull) {
                    query = getRequestUrl(requestObject.get("url").asJsonObject)
                }
            }

            var headers = listOf<RequestHeader>()
            if (requestObject.has("header")) {
                if (!requestObject.get("header").asJsonArray.isEmpty) {
                    headers = getRequestHeaders(requestObject.get("header").asJsonArray)
                }
            }

            var body = RequestBody(type = BodyType.NONE)
            if (requestObject.has("body")) {
                body = getRequestBody(requestObject.get("body").asJsonObject)
            }

            val newRequest = Request(
                method = RequestMethod.valueOf(requestObject.get("method").asString),
                query = query,
                headers = headers,
                body = body
            )

            return RequestItem(
                id = get("id").asString,
                name = get("name").asString,
                description = itemDescription,
                request = newRequest,
                parentId = requestGroup.id
            )
        }
    }

    private fun getRequestUrl(urlObject: JsonObject): RequestQuery {
        urlObject.run {
            return if (has("raw")) {
                RequestQuery(get("raw").asString)
            } else {
                RequestQuery()
            }
        }
    }

    private fun getRequestHeaders(headersArray: JsonArray): List<RequestHeader> {
        val headersList: MutableList<RequestHeader> = mutableListOf()
        if (headersArray.isEmpty) return headersList

        for (header in headersArray) {
            val headerObject = header.asJsonObject
            headerObject.run {
                val key = get("key").asString
                val value = get("value").asString

                var enabled = true
                if (has("disabled")) {
                    enabled = !get("disabled").asBoolean
                }

                var description: String? = null
                if (has("description")) {
                    description = get("description").asString
                }

                val newHeader = RequestHeader(key = key, value = value, enabled = enabled, description = description)
                headersList.add(newHeader)
            }
        }

        return headersList
    }

    private fun getRequestBody(bodyObject: JsonObject): RequestBody {
        bodyObject.run {
            if (!has("mode")) {
                return RequestBody(type = BodyType.NONE)
            }

            val bodyType = get("mode").asString
            when (bodyType) {
                "formdata" -> {
                    if (has("formdata")) {
                        val formDataList: MutableList<RequestFormData> = mutableListOf()
                        val formDataJsonObjects = get("formdata").asJsonArray

                        for (formData in formDataJsonObjects) {
                            val formDataObject = formData.asJsonObject

                            formDataObject.run {
                                val key = get("key").asString
                                val value = get("value").asString

                                var enabled = true
                                if (has("disabled")) {
                                    enabled = get("disabled").asBoolean
                                }

                                var description: String? = null
                                if (has("description")) {
                                    description = get("description").asString
                                }

                                val newFormDataItem = RequestFormData(
                                    key = key,
                                    value = value,
                                    enabled = enabled,
                                    description = description
                                )
                                formDataList.add(newFormDataItem)
                            }
                        }

                        return RequestBody(enabled = true, type = BodyType.FORMDATA, formData = formDataList)
                    }
                }
                "urlencoded" -> {
                    if (has("urlencoded")) {
                        val multipartDataList: MutableList<RequestMultipartData> = mutableListOf()
                        val multipartDataJsonObjects = get("urlencoded").asJsonArray

                        for (multipartData in multipartDataJsonObjects) {
                            val multipartDataObject = multipartData.asJsonObject

                            multipartDataObject.run {
                                val key = get("key").asString
                                val value = get("value").asString

                                var enabled = true
                                if (has("disabled")) {
                                    enabled = get("disabled").asBoolean
                                }

                                var description: String? = null
                                if (has("description")) {
                                    description = get("description").asString
                                }

                                val newFormDataItem = RequestMultipartData(
                                    key = key,
                                    value = value,
                                    enabled = enabled,
                                    description = description
                                )
                                multipartDataList.add(newFormDataItem)
                            }
                        }

                        return RequestBody(enabled = true, type = BodyType.MULTIPART, multipartData = multipartDataList)
                    }
                }
                "raw" -> {
                    var rawData = ""
                    if (has("raw")) {
                        rawData = get("raw").asString
                    }

                    return RequestBody(enabled = true, type = BodyType.RAW, rawData = RequestRawData(rawData))
                }
                "file" -> {
                    var file: RequestFile? = null
                    if (has("file")) {
                        val fileObject = get("file").asJsonObject
                        if (fileObject.has("src")) {
                            val src = fileObject.get("src").asString
                            file = RequestFile(name = src, path = src, uri = src, mimeType = "", size = 0)
                        }
                    }

                    return RequestBody(type = BodyType.BINARY, binaryData = RequestBinaryData(file = file))
                }
            }
        }

        return RequestBody(type = BodyType.NONE)
    }
}