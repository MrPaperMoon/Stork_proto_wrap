package io.stork.client.exceptions

import io.stork.proto.error.AuthenticationError

class AuthenticationException(val error: AuthenticationError): StorkApiException(error.message) {
    val type: AuthenticationError.AuthenticationErrorType = error.errorType

    companion object {
        const val CODE = 403
    }
}