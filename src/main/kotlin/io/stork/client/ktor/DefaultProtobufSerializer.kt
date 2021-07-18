package io.stork.client.ktor

import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import io.ktor.http.*
import io.ktor.http.content.*
import kotlin.reflect.KClass

object DefaultProtobufSerializer : ProtobufSerializer {
    override fun write(payload: Message<*, *>, contentType: ContentType): ByteArrayContent {
        return ByteArrayContent(payload.encode(), contentType)
    }

    override fun <T : Message<*, *>> read(type: KClass<out T>, bytes: ByteArray): T {
        return bytes.decodeAsProto(type)
    }

    private fun <T : Message<*, *>> ByteArray.decodeAsProto(bodyType: KClass<T>): T {
        return ProtoAdapter.get(bodyType.java).decode(this)
    }
}
