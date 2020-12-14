package io.stork.client

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory

data class ApiClientConfig(
    val apiBaseUrl: String = "https://stork.team/api/",
    val websocketUrl: String = "wss://stork.team/ws/event",
    val mediaType: ApiMediaType = ApiMediaType.PROTOBUF,
    val logLevel: LogLevel = LogLevel.NONE
)

enum class ApiMediaType(internal val contentType: String, internal val converterFactory: Converter.Factory) {
    PROTOBUF("application/x-protobuf", ProtoConverterFactory.create()),
    JSON("application/json", JacksonConverterFactory.create(objectMapper))
}

enum class LogLevel(internal val impl: HttpLoggingInterceptor.Level) {
    NONE(HttpLoggingInterceptor.Level.NONE),
    BASIC(HttpLoggingInterceptor.Level.BASIC),
    HEADERS(HttpLoggingInterceptor.Level.HEADERS),
    BODY(HttpLoggingInterceptor.Level.BODY)
}