package com.hickar.restly.models

import java.io.IOException

class UserExistsException(message: String? = null) : IOException(message)
class NotStrongPasswordException(message: String? = null) : IOException(message)
class InvalidEmailException(message: String? = null) : IOException(message)
class ExpiredJwtException(message: String? = null) : IOException(message)
class EmptyAuthResponseBodyException(message: String? = null) : IOException(message)
class InvalidCredentialsException(message: String? = null) : IOException(message)
class WrongApiKeyException(message: String? = null) : IOException(message)
class NetworkUnavailableException(message: String? = null) : IOException(message)
class PostmanApiLimitExceeded(message: String? = null) : IOException(message)