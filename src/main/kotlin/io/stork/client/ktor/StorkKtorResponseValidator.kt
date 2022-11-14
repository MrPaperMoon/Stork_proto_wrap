package io.stork.client.ktor

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.stork.client.ApiResult
import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.ValidationException

suspend inline fun <reified T : Any> HttpResponse.getResult(): ApiResult<T> {
    val code = status.value
    return try {
        when (code) {
            200 -> ApiResult.Success(receive())
            AuthenticationException.CODE -> ApiResult.AuthenticationError(receive())
            ValidationException.CODE -> ApiResult.ValidationError(receive())
            UnknownException.CODE -> ApiResult.UnknownError(receive())
            else -> ApiResult.UnknownRemoteError(code, status.description)
        }
    } catch (ex: Exception) {
        ApiResult.UnknownClientError(code, status.description, ex)
    }
}
