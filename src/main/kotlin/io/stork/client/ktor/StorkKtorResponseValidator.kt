package io.stork.client.ktor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.ValidationException
import io.stork.proto.error.UnhandledError

fun HttpClientConfig<*>.StorkKtorResponseValidator() {
    HttpResponseValidator {
        validateResponse { response ->
            val failure =  response.validate()
            if (failure != null) {
                throw failure
            }
        }
    }
}

private suspend fun HttpResponse.validate(): Exception? {
    try {
        return when (status.value) {
            200 -> null
            AuthenticationException.CODE -> AuthenticationException(receive())
            ValidationException.CODE -> ValidationException(receive())
            UnknownException.CODE -> UnknownException(receive())
            else -> UnknownException(asUnhandledError())
        }
    } catch (ex: Exception) {
        return UnknownException(asUnhandledError())
    }
}

private fun HttpResponse.asUnhandledError(): UnhandledError {
    return UnhandledError.newBuilder()
        .setName("Code ${status.value}")
        .setMessage(status.description)
        .build()
}