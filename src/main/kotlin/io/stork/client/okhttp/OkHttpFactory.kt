package io.stork.client.okhttp

import io.stork.client.ApiClientConfig
import io.stork.client.SessionProvider
import okhttp3.OkHttpClient

object OkHttpFactory {
    fun createOkHttp(config: ApiClientConfig, sessionProvider: SessionProvider): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionProvider::sessionId))
            .addInterceptor(ContentTypeInterceptor(config.mediaType.contentType))
            .build()
    }
}