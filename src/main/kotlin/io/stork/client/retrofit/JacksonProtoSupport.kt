package io.stork.client.retrofit

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import java.io.IOException


class ProtobufSerializer : JsonSerializer<Message>() {
    @Throws(IOException::class)
    override fun serialize(message: Message, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeRawValue(JsonFormat.printer().print(message))
    }
}
