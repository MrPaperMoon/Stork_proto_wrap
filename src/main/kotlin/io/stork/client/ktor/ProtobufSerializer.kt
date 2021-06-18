package io.stork.client.ktor

import com.google.protobuf.Message
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.core.*
import kotlin.reflect.KClass

interface ProtobufSerializer {
    fun write(payload: Message, contentType: ContentType): ByteArrayContent
    fun write(data: Message): ByteArrayContent = write(data, ContentType.Application.ProtoBuf)

    fun read(type: KClass<out Message>, body: Input): Message
}