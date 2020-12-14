package io.stork.client

import okhttp3.logging.HttpLoggingInterceptor

data class ApiClientConfig(
    val apiBaseUrl: String = "https://stork.team/api/",
    val websocketUrl: String = "wss://stork.team/ws/event",
    val mediaType: ApiMediaType = ApiMediaType.PROTOBUF,
    val logLevel: LogLevel = LogLevel.NONE
)

enum class ApiMediaType(internal val contentType: String) {
    PROTOBUF("application/x-protobuf"),
    JSON("application/json")
}

enum class LogLevel(internal val impl: HttpLoggingInterceptor.Level) {
    NONE(HttpLoggingInterceptor.Level.NONE),
    BASIC(HttpLoggingInterceptor.Level.BASIC),
    BODY(HttpLoggingInterceptor.Level.BODY)
}