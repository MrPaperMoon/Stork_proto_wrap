package io.stork.client.okhttp

import okhttp3.Interceptor
import okhttp3.Response

internal class ContentTypeInterceptor(private val contentType: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("Accept", contentType)
                .addHeader("Content-Type", contentType)
                .build()
        )
    }
}