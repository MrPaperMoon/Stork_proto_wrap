package io.stork.client.exceptions

import io.stork.client.CloseReason
import java.io.IOException

class ConnectionClosedException(val closedReason: CloseReason, message: String, cause: Throwable? = null): IOException(message, cause)