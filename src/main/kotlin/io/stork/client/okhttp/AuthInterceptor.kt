package io.stork.client.okhttp

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class AuthInterceptor(private val accessTokenProvider: () -> String?) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = accessTokenProvider()

        val request: Request = chain.request()
        val authenticatedRequest: Request = when {
            accessToken != null -> request.withAuthorization(accessToken)
            else -> request
        }

        return chain.proceed(authenticatedRequest)
    }

    private fun Request.withAuthorization(token: String): Request {
        return newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }
}