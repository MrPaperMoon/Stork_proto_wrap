package io.stork.client.retrofit

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.protobuf.Message
import retrofit2.converter.jackson.JacksonConverterFactory

private val storkJacksonModule = SimpleModule("Stork").apply {
    addSerializer(Message::class.java, ProtobufSerializer())
}


private val objectMapper: ObjectMapper = ObjectMapper().apply {
    registerModule(storkJacksonModule)
}

val ProtoJsonConverterFactory = JacksonConverterFactory.create(objectMapper)