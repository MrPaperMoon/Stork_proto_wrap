package io.stork.client

import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.ValidationException
import io.stork.proto.error.UnhandledError

sealed class Result<out T: Any> {
    data class Success<T: Any>(val response: T): Result<T>()
    data class AuthenticationError(val response: io.stork.proto.error.AuthenticationError): Result<Nothing>()
    data class ValidationError(val response: io.stork.proto.error.ValidationError): Result<Nothing>()
    data class UnknownError(val response: UnhandledError): Result<Nothing>()

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
            return UnhandledError.newBuilder()
                .setName(javaClass.name)
                .setMessage(message + ":\n" + stackTraceToString())
                .build()
        }

        fun <T: Any> fromException(ex: Exception): Result<T> {
            return UnknownError(ex.asUnhandledError())
        }
    }
}