package io.stork.client.ktor

import com.google.protobuf.Message
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.core.*
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

object DefaultProtobufSerializer : ProtobufSerializer {
    private val builderCache: MutableMap<Class<*>, Method> = ConcurrentHashMap<Class<*>, Method>()

    override fun write(payload: Message, contentType: ContentType): ByteArrayContent {
        return ByteArrayContent(payload.toByteArray(), contentType)
    }

    override fun read(type: KClass<out Message>, body: Input): Message {
        return body.readBytes().decodeAsProto(type)
    }


    private fun <T : Message> ByteArray.decodeAsProto(bodyType: KClass<T>): T {
        val builder: Message.Builder = getProtoBuilder(bodyType.java)
        builder.mergeFrom(this)
        return bodyType.cast(builder.build())
    }

    private fun getProtoBuilder(clazz: Class<out Message>): Message.Builder {
        return getMethod(clazz).invoke(clazz) as Message.Builder
    }

    private fun getMethod(clazz: Class<out Message>): Method = synchronized(builderCache) {
        return builderCache[clazz] ?: clazz.getMethod("newBuilder").also {
            builderCache[clazz] = it
        }
    }
}
