package io.stork.client.retrofit

import io.stork.client.exceptions.AuthenticationException
import io.stork.client.exceptions.UnknownException
import io.stork.client.exceptions.ValidationException
import io.stork.proto.error.AuthenticationError
import io.stork.proto.error.UnhandledError
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class ResponseHandler(private val retrofit: Retrofit) {
    fun <T> Response<T>.getResult(): Result<T> = when (code()) {
        200 -> success(body()!!)
        else -> failure(getError())
    }

    private fun Response<*>.getError(): Exception {
        val errorBody = errorBody()
        val code = code()
        return when {
            errorBody == null -> UnknownException(asUnhandledError())
            code == AuthenticationException.CODE -> AuthenticationException(errorBody.parse())
            code == ValidationException.CODE -> ValidationException(errorBody.parse())
            code == UnknownException.CODE -> UnknownException(errorBody.parse())
            else -> UnknownException(asUnhandledError())
        }
    }

    private fun Response<*>.asUnhandledError(): UnhandledError = UnhandledError.newBuilder()
            .setName("Code ${code()}")
            .setMessage(message())
            .build()

    private inline fun <reified T> ResponseBody.parse(): T {
        val converter: Converter<ResponseBody, T> = getResponseBodyConverter()
        return converter.convert(this)!!
    }

    private inline fun <reified T> getResponseBodyConverter(): Converter<ResponseBody, T> {
        val responseType: Type = T::class.java
        return try {
            retrofit.responseBodyConverter(responseType, emptyArray())
        } catch (e: RuntimeException) { // Wide exception range because factories are user code.
            throw IllegalStateException("Unable to create converter for $responseType")
        }
    }
}