package io.stork.client

import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.ValidationException
import io.stork.proto.error.UnhandledError

sealed class ApiResult<out T: Any> {
    data class Success<T: Any>(val response: T): ApiResult<T>()
    data class AuthenticationError(val response: io.stork.proto.error.AuthenticationError): ApiResult<Nothing>()
    data class ValidationError(val response: io.stork.proto.error.ValidationError): ApiResult<Nothing>()
    data class UnknownError(val response: UnhandledError): ApiResult<Nothing>()

    fun getOrThrow(): T = when (this) {
        is Success -> response
        is AuthenticationError -> throw AuthenticationException(response)
        is ValidationError -> throw ValidationException(response)
        is UnknownError -> throw UnknownException(response)
    }

    fun getAnyResult(): Any = when (this) {
        is Success -> response
        is AuthenticationError -> response
        is ValidationError -> response
        is UnknownError -> response
    }

    companion object {
        private fun Exception.asUnhandledError(): UnhandledError {
            return UnhandledError(
                    name = javaClass.name,
                    message = message + ":\n" + stackTraceToString()
            )
        }

        fun <T: Any> fromException(ex: Exception): ApiResult<T> {
            return UnknownError(ex.asUnhandledError())
        }
    }
}