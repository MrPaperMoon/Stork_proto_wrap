package io.stork.client.exceptions

import io.stork.proto.client.error.ValidationError

class ValidationException(val error: ValidationError) : StorkApiException(error.describe()) {
    companion object {
        const val CODE = 400

        private fun ValidationError.describe(): String = violations.joinToString {
            it.field_ + " - " + it.message
        }
    }
}