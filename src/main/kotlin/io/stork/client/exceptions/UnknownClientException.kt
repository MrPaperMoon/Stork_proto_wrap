package io.stork.client.exceptions

class UnknownClientException(code: Int,
                             description: String,
                             exception: Exception) : StorkApiUncheckedException("Code $code: $description\n${exception.message}", exception)