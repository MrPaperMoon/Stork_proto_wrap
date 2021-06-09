package io.stork.client.ktor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.ValidationException
import io.stork.proto.error.UnhandledError
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(KtorApiClientSerializer::class.java)

fun HttpClientConfig<*>.StorkKtorResponseValidator() {
    HttpResponseValidator {
        validateResponse {} // always ok - we handle errors manually due to receive() call recursion
    }
}

suspend fun HttpResponse.throwIfNotOk(): HttpResponse {
    val failure = validate()
    if (failure != null) {
        throw failure
    } else {
        return this
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
        log.error("Failed to receive correct body: ", ex)
        return UnknownException(ex.asUnhandledError())
    }
}

private fun HttpResponse.asUnhandledError(): UnhandledError {
    return UnhandledError.newBuilder()
        .setName("Code ${status.value}")
        .setMessage(status.description)
        .build()
}

private fun Exception.asUnhandledError(): UnhandledError {
    return UnhandledError.newBuilder()
        .setName(javaClass.name)
        .setMessage(message + ":\n" + stackTraceToString())
        .build()
}