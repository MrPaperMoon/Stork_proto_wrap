package io.stork.client.ktor

import com.google.protobuf.Message
import kotlin.reflect.KClass

interface KtorApiClientDeserializer {
    fun <T: Message> deserialize(type: KClass<T>, body: ByteArray): T
}

inline fun <reified T: Message> KtorApiClientDeserializer.deserialize(body: ByteArray): T =
    deserialize(T::class, body)