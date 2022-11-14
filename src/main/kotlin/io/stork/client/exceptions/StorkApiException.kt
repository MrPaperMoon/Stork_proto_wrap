package io.stork.client.exceptions

abstract class StorkApiException(message: String,
                                 cause: Throwable? = null) : Exception(message, cause)

abstract class StorkApiUncheckedException(message: String,
                                          cause: Throwable? = null) : StorkApiException(message, cause)