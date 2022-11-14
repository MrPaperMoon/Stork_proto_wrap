package io.stork.client.exceptions

import io.stork.proto.client.error.UnhandledError

class UnknownException(val error: UnhandledError) : StorkApiUncheckedException(error.name + ": " + error.message) {
    companion object {
        const val CODE = 500
    }
}