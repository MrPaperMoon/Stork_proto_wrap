package io.stork.client.exceptions

import io.stork.proto.error.AuthenticationError
import io.stork.proto.error.ValidationError

class ValidationException(val error: ValidationError): StorkApiException(error.describe()) {
    companion object {
        const val CODE = 400

        private fun ValidationError.describe(): String = violations.joinToString {
            it.field_ + " - " + it.message
        }
    }
}