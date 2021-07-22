package io.stork.client

import okhttp3.logging.HttpLoggingInterceptor
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class ApiClientConfig(
    val domainName: String = StorkServers.production.address,
    val useSsl: Boolean = true,
    val mediaType: ApiMediaType = ApiMediaType.PROTOBUF,
    val logLevel: LogLevel = LogLevel.NONE,
    val timeout: Duration = Duration.minutes(1)
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
    val websocketUrl: String = "$wsProtocol://$domainName/ws"
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