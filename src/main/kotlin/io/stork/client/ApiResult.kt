package io.stork.client

import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownClientException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.UnknownRemoteException
import io.stork.client.exceptions.ValidationException
import io.stork.proto.client.error.UnhandledError

sealed class ApiResult<out T : Any> {
    open val response: T? = null

    data class Success<T : Any>(override val response: T) : ApiResult<T>()
    data class AuthenticationError(val error: io.stork.proto.client.error.AuthenticationError) : ApiResult<Nothing>()
    data class ValidationError(val error: io.stork.proto.client.error.ValidationError) : ApiResult<Nothing>()

    sealed class UncheckedError : ApiResult<Nothing>()
    data class UnknownError(val error: UnhandledError) : UncheckedError()
    data class UnknownClientError(val code: Int,
                                  val description: String,
                                  val exception: Exception) : UncheckedError()

    data class UnknownRemoteError(val code: Int, val description: String) : UncheckedError()

    fun getOrThrow(): T = when (this) {
        is Success -> response
        is AuthenticationError -> throw AuthenticationException(error)
        is ValidationError -> throw ValidationException(error)
        is UnknownError -> throw UnknownException(error)
        is UnknownClientError -> throw UnknownClientException(code, description, exception)
        is UnknownRemoteError -> throw UnknownRemoteException(code, description)
    }
}