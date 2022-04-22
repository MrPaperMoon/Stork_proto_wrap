package io.stork.client.okhttp

import io.stork.client.ApiClientConfig
import io.stork.client.SessionProvider
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

object OkHttpFactory {
    fun createOkHttp(config: ApiClientConfig, sessionProvider: SessionProvider): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionProvider::sessionId))
            .addInterceptor(ContentTypeInterceptor(config.mediaType.contentType))
            .pingInterval(30, TimeUnit.SECONDS)
            .build()
    }
}