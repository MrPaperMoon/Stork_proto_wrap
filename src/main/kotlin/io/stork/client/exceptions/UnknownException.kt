package io.stork.client.exceptions

import io.stork.proto.client.error.UnhandledError

class UnknownException(val error: UnhandledError) : StorkApiException(error.name + ": " + error.message) {
    companion object {
        const val CODE = 500
    }
}
