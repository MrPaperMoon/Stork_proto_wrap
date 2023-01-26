package io.stork.client

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class ApiClientConfig(
    val domainNameProvider: () -> String,
    val useSsl: Boolean = true,
    val mediaType: ApiMediaType = ApiMediaType.PROTOBUF,
    val logLevel: LogLevel = LogLevel.NONE,
    val timeout: Duration = 30.seconds,
) {

    private val httpProtocol = when {
        useSsl -> "https"
        else -> "http"
    }

    private val wsProtocol = when {
        useSsl -> "wss"
        else -> "ws"
    }

    val domainName: String
        get() = domainNameProvider()

    val apiBaseUrl: String
        get() = "$httpProtocol://$domainName/api"
    val websocketUrl: String
        get() = "$wsProtocol://$domainName/ws"
}

enum class ApiMediaType(internal val contentType: String) {
    PROTOBUF("application/x-protobuf"),
    JSON("application/json")
}

enum class LogLevel {
    NONE,
    BASIC,
    BODY
}