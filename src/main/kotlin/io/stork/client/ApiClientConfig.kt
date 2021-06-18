package io.stork.client

import okhttp3.logging.HttpLoggingInterceptor

data class ApiClientConfig(
    val domainName: String = "stork.ai",
    val useSsl: Boolean = true,
    val mediaType: ApiMediaType = ApiMediaType.PROTOBUF,
    val logLevel: LogLevel = LogLevel.NONE
) {
    private val httpProtocol = when {
        useSsl -> "https"
        else -> "http"
    }

    private val wsProtocol = when {
        useSsl -> "wss"
        else -> "ws"
    }

    val apiBaseUrl: String = "$httpProtocol://$domainName/api"
    val websocketUrl: String = "$wsProtocol://$domainName/ws/event"
}

enum class ApiMediaType(internal val contentType: String) {
    PROTOBUF("application/x-protobuf"),
    JSON("application/json")
}

enum class LogLevel(internal val impl: HttpLoggingInterceptor.Level) {
    NONE(HttpLoggingInterceptor.Level.NONE),
    BASIC(HttpLoggingInterceptor.Level.BASIC),
    BODY(HttpLoggingInterceptor.Level.BODY)
}