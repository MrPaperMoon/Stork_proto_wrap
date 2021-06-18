package io.stork.client.ktor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import io.stork.client.Result
import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.ValidationException
import io.stork.proto.error.UnhandledError

fun HttpClientConfig<*>.StorkKtorResponseValidator() {
    HttpResponseValidator {
        validateResponse {} // always ok - we handle errors manually due to receive() call recursion
    }
}

suspend inline fun <reified T: Any> HttpResponse.getResult(): Result<T> {
    try {
        return when (status.value) {
            200 -> Result.Success(receive())
            AuthenticationException.CODE -> Result.AuthenticationError(receive())
            ValidationException.CODE -> Result.ValidationError(receive())
            UnknownException.CODE -> Result.UnknownError(receive())
            else -> Result.UnknownError(asUnhandledError())
        }
    } catch (ex: Exception) {
        return Result.fromException(ex)
    }
}

fun HttpResponse.asUnhandledError(): UnhandledError {
    return UnhandledError.newBuilder()
        .setName("Code ${status.value}")
        .setMessage(status.description)
        .build()
}