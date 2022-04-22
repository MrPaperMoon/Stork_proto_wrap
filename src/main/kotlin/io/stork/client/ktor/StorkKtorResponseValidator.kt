package io.stork.client.ktor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import io.stork.client.ApiResult
import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.ValidationException
import io.stork.proto.client.error.UnhandledError

fun HttpClientConfig<*>.StorkKtorResponseValidator() {
    expectSuccess = false
    HttpResponseValidator {
        validateResponse {} // always ok - we handle errors manually due to receive() call recursion
    }
}

suspend inline fun <reified T : Any> HttpResponse.getResult(): ApiResult<T> {
    try {
        return when (status.value) {
            200 -> ApiResult.Success(receive())
            AuthenticationException.CODE -> ApiResult.AuthenticationError(receive())
            ValidationException.CODE -> ApiResult.ValidationError(receive())
            UnknownException.CODE -> ApiResult.UnknownError(receive())
            else -> ApiResult.UnknownError(asUnhandledError())
        }
    } catch (ex: Exception) {
        return ApiResult.fromException(ex)
    }
}

fun HttpResponse.asUnhandledError(): UnhandledError {
    return UnhandledError(
        name = "Code ${status.value}",
        message = status.description
    )
}
