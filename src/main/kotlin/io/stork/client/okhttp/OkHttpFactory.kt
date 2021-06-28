package io.stork.client.okhttp

import io.stork.client.ApiClientConfig
import io.stork.client.SessionManager
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttpFactory {
    fun createOkHttp(config: ApiClientConfig, sessionManager: SessionManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager::sessionJwtToken))
            .addInterceptor(ContentTypeInterceptor(config.mediaType.contentType))
            .pingInterval(30, TimeUnit.SECONDS)
            .build()
    }
}