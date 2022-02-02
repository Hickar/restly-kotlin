package com.hickar.restly.repository.serializers

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

            itemObject.apply {
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

                if (has("auth")) {
                    val authObject = get("auth").asJsonObject
                    requestGroup.auth = buildAuth(authObject) ?: RequestAuth()
                }
            }
        }
    }

    private fun getRequestItem(requestItemObject: JsonObject, requestGroup: RequestDirectory): RequestItem {
        requestItemObject.apply {
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

            var auth: RequestAuth? = null
            if (requestObject.has("auth")) {
                auth = buildAuth(requestObject.get("auth").asJsonObject)
            }

            val newRequest = Request(
                method = RequestMethod.valueOf(requestObject.get("method").asString),
                query = query,
                headers = headers,
                body = body,
                auth = auth
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
        urlObject.apply {
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
        bodyObject.apply {
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

                            formDataObject.apply {
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

    private fun buildAuth(authObj: JsonObject): RequestAuth? {
        authObj.run {
            if (!has("type")) return null


            val authType = get("type").asString
            if (authType == "noAuth") {
                return RequestAuth(type = RequestAuthType.NO_AUTH)
            }

            val authEntryFields = authObj.get(authType).asJsonArray

            return when (authType) {
                "basic" -> return buildBasicAuth(authEntryFields)
                "awsv4" -> return buildAwsAuth(authEntryFields)
                "apiKey" -> return buildApiKeyAuth(authEntryFields)
                "bearer" -> return buildBearerAuth(authEntryFields)
                "digest" -> return buildDigestAuth(authEntryFields)
                "edgegrid" -> return buildEdgegrid(authEntryFields)
                "hawk" -> return buildHawkAuth(authEntryFields)
                "ntlm" -> return buildNtlmAuth(authEntryFields)
                "oauth1" -> return buildOAuth1Auth(authEntryFields)
                "oauth2" -> return buildOAuth2Auth(authEntryFields)
                else -> null
            }
        }
    }

//  TODO: somehow generalize all underlying methods
    private fun buildBasicAuth(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.BASIC,
            basic = RequestAuthBasic(
                username = fields["username"] ?: "",
                password = fields["password"] ?: ""
            )
        )
    }

    private fun buildAwsAuth(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.AWS,
            awsv4 = RequestAuthAWS(
                accessKey = fields["accessKey"] ?: "",
                secretKey = fields["secretKey"] ?: "",
                region = fields["region"] ?: "",
                serviceName = fields["service"] ?: "",
                sessionToken = fields["sessionToken"] ?: ""
            )
        )
    }

    private fun buildApiKeyAuth(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.API_KEY,
            apiKey = RequestAuthApiKey(
                key = fields["key"] ?: "",
                value = fields["value"] ?: ""
            )
        )
    }

    private fun buildBearerAuth(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.BEARER,
            bearer = fields["token"]
        )
    }

    private fun buildDigestAuth(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.DIGEST,
            digest = RequestAuthDigest(
                username = fields["username"] ?: "",
                password = fields["password"] ?: "",
                realm = fields["realm"] ?: "",
                nonce = fields["nonce"] ?: "",
                nonceCount = fields["nonceCount"] ?: "",
                algorithm = fields["algorithm"] ?: "",
                qop = fields["qop"] ?: "",
                clientNonce = fields["clientNonce"] ?: "",
                opaque = fields["opaque"] ?: ""
            )
        )
    }

    private fun buildEdgegrid(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.EDGEGRID,
            edgegrid = RequestAuthEdgegrid(
                accessToken = fields["accessToken"] ?: "",
                clientToken = fields["clientToken"] ?: "",
                clientSecret = fields["clientSecret"] ?: "",
                nonce = fields["nonce"] ?: "",
                timestamp = fields["timestamp"] ?: "",
                baseUrl = fields["baseURL"] ?: "",
                headersToSign = fields["headersToSign"] ?: ""
            )
        )
    }

    private fun buildOAuth1Auth(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.OAUTH1,
            oauth1 = RequestAuthOAuth1(
                signatureMethod = fields["signature"] ?: "",
                consumerKey = fields["consumerKey"] ?: "",
                consumerSecret = fields["consumerSecret"] ?: "",
                accessToken = fields["token"] ?: "",
                tokenSecret = fields["tokenSecret"] ?: "",
                realm = fields["realm"] ?: "",
                nonce = fields["nonce"] ?: "",
                timestamp = fields["timestamp"] ?: "",
                verifier = fields["verifier"] ?: "",
                callbackUrl = fields["callback"] ?: "",
                version = fields["version"] ?: "",
                addParamsToHeader = fields["addParamsToHeader"].toBoolean(),
                includeBodyHash = fields["includeBodyHash"].toBoolean(),
                addEmptyParamsToSignature = fields["addEmptyParamsToSign"].toBoolean()
            )
        )
    }

//  TODO: Deserialize "resources" and "audience" lists to corresponding properties
    private fun buildOAuth2Auth(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.OAUTH2,
            oauth2 = RequestAuthOAuth2(
                accessToken = fields["accessToken"] ?: "",
                headerPrefix = fields["headerPrefix"] ?: "",
                tokenName = fields["tokenName"] ?: "",
                grantType = fields["grant_type"] ?: "",
                callbackUrl = fields["redirect_uri"] ?: "",
                authUrl = fields["authUrl"] ?: "",
                authTokenUrl = fields["authTokenUrl"] ?: "",
                clientId = fields["clientId"] ?: "",
                clientSecret = fields["clientSecret"] ?: "",
                scope = fields["scope"] ?: "",
                state = fields["state"] ?: "",
                clientAuth = fields["client_authentication"] ?: "",
                addAuthTo = fields["addTokenTo"] ?: "",
            )
        )
    }

    private fun buildHawkAuth(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.HAWK,
            hawk = RequestAuthHawk(
                authId = fields["authId"] ?: "",
                authKey = fields["authKey"] ?: "",
                user = fields["user"] ?: "",
                algorithm = fields["algorithm"] ?: "",
                nonce = fields["nonce"] ?: "",
                ext = fields["extraData"] ?: "",
                app = fields["app"] ?: "",
                dlg = fields["delegation"] ?: "",
                timestamp = fields["timestamp"] ?: "",
                includePayloadHash = fields["includePayloadHash"].toBoolean()
            )
        )
    }

    private fun buildNtlmAuth(authFields: JsonArray): RequestAuth {
        val fields = buildAuthFieldsMap(authFields)
        return RequestAuth(
            type = RequestAuthType.NTLM,
            ntlm = RequestAuthNTLM(
                username = fields["username"] ?: "",
                password = fields["password"] ?: "",
                domain = fields["domain"] ?: "",
                workstation = fields["workstation"] ?: ""
            )
        )
    }

    private fun buildAuthFieldsMap(authFields: JsonArray): Map<String, String> {
        val fieldsMap = mutableMapOf<String, String>()
        for (authField in authFields) {
            authField.asJsonObject.apply {
                if (has("key") && has("value")) {
                    fieldsMap[get("key").asString] = get("value").asString
                }
            }
        }

        return fieldsMap
    }
}