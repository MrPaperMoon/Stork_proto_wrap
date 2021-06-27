package io.stork.client.okhttp

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import java.io.IOException

class ProtobufSerializer : JsonSerializer<Message>() {
    @Throws(IOException::class)
    override fun serialize(message: Message, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeRawValue(JsonFormat.printer().print(message))
    }
}

private val storkJacksonModule = SimpleModule("Stork").apply {
    addSerializer(Message::class.java, ProtobufSerializer())
}

internal val objectMapper: ObjectMapper = ObjectMapper().apply {
    registerModule(storkJacksonModule)
}