package com.hickar.restly.models

data class RequestAuth(
    var type: String = "",
    var basic: RequestAuthBasic? = null,
    var awsv4: RequestAuthAWS? = null,
    var apiKey: RequestAuthApiKey? = null,
    var bearer: String? = null,
    var digest: RequestAuthDigest? = null,
    var edgegrid: RequestAuthEdgegrid? = null,
    var hawk: RequestAuthHawk? = null,
    var ntlm: RequestAuthNTLM? = null,
    var oauth1: RequestAuthOAuth1? = null,
    var oauth2: RequestAuthOAuth2? = null
)

data class RequestAuthApiKey(
    var key: String = "",
    var value: String = "",
    var addTo: String = "header"
)

data class RequestAuthBasic(
    var username: String = "",
    var password: String = ""
)

data class RequestAuthOAuth1(
    var signatureMethod: String = "",
    var consumerKey: String = "",
    var consumerSecret: String = "",
    var accessToken: String = "",
    var tokenSecret: String = "",
    var callbackUrl: String = "",
    var verifier: String = "",
    var timestamp: Long = 0L,
    var nonce: String = "",
    var version: String = "1.0",
    var realm: String = "",
    var addAuthTo: String = ""
)

data class RequestAuthOAuth2(
    var accessToken: String = "",
    var headerPrefix: String = "Bearer ",
    var tokenName: String = "",
    var grantType: String = "",
    var callbackUrl: String = "",
    var authUrl: String = "",
    var authTokenUrl: String = "",
    var clientId: String = "",
    var clientSecret: String = "",
    var scope: String = "",
    var state: String = "",
    var clientAuth: String = "",
    var addAuthTo: String = "",
    var targetServices: List<String> = listOf(),
    var targetAudience: List<String> = listOf()
)

data class RequestAuthDigest(
    var username: String = "",
    var password: String = "",
    var realm: String = "",
    var nonce: String = "",
    var nonceCount: Int = 0,
    var algorithm: String = "",
    var qop: Int = 0,
    var clientNonce: String = "",
    var opaque: String = ""
)

data class RequestAuthHawk(
    var authId: String = "",
    var authKey: String = "",
    var algorithm: String = "",
    var user: String = "",
    var nonce: String = "",
    var ext: String = "",
    var app: String = "",
    var dlg: String = "",
    var timestamp: Long,
    var includePayloadHash: Boolean = false
)

data class RequestAuthAWS(
    var accessKey: String = "",
    var secretKey: String = "",
    var region: String = "",
    var serviceName: String = "",
    var serviceToken: String = "",
    var addAuthTo: String = ""
)

data class RequestAuthNTLM(
    var username: String = "",
    var password: String = "",
    var domain: String = "",
    var workstation: String = ""
)

data class RequestAuthEdgegrid(
    var accessToken: String = "",
    var clientToken: String = "",
    var clientSecret: String = "",
    var nonce: String = "",
    var timestamp: Long = 0L,
    var baseUrl: String = "",
    var headersToSign: String = ""
)