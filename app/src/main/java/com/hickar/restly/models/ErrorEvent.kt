package com.hickar.restly.models

import androidx.annotation.StringRes
import com.hickar.restly.R

enum class ErrorEvent(
    @StringRes val title: Int,
    @StringRes val message: Int
) {
    ConnectionRefused(R.string.conn_refused_error_name, R.string.conn_refused_error_description),
    ConnectionTimeout(R.string.conn_timeout_error_name, R.string.conn_timeout_error_description),
    ConnectionUnexpected(R.string.conn_unexpected_error_name, R.string.conn_unexpected_error_description),
    PostmanAuthError(R.string.postman_auth_error_name, R.string.postman_auth_error_description),
    UnknownHostError(R.string.unknown_host_error_name, R.string.unknown_host_error_description),
    NoInternetConnectionError(R.string.no_internet_conn_error_name, R.string.no_internet_conn_error_description),
    UnexpectedUrlScheme(R.string.unexpected_url_scheme_error_name, R.string.unexpected_url_scheme_error_description),
    EmptyUrl(R.string.empty_url_error_name, R.string.empty_url_error_description),
    SizeExceedsLimit(R.string.size_exceeds_limit_error_name, R.string.size_exceeds_limit_error_description),
    RequestCallTimeout(R.string.request_call_timeout_error_name, R.string.request_call_timeout_error_description),
    NotStrongPassword(R.string.not_strong_password_error_name, R.string.not_strong_password_error_description),
    InvalidCredentials(R.string.invalid_credentials_error_name, R.string.invalid_credentials_error_description),
    UserExists(R.string.user_exists_error_name, R.string.user_exists_error_description)
}