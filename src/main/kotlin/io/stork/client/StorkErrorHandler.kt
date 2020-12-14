package io.stork.client

import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.ValidationException
import io.stork.proto.error.UnhandledError
import kotlin.reflect.KClass

object StorkErrorHandler {
    fun handle(code: Int, errorBody: ErrorBody): Exception {
        return when (code) {
            AuthenticationException.CODE -> AuthenticationException(errorBody.parse())
            ValidationException.CODE -> ValidationException(errorBody.parse())
            UnknownException.CODE -> UnknownException(errorBody.parse())
            else -> UnknownException(errorBody.asUnhandledError())
        }
    }

    interface ErrorBody {
        fun <T: Any> parse(asType: KClass<T>): T
        fun asUnhandledError(): UnhandledError
    }

    inline fun <reified T: Any> ErrorBody.parse(): T = parse(T::class)
}
