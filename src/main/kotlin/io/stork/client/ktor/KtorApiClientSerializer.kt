package io.stork.client.ktor

import com.google.protobuf.Message

interface KtorApiClientSerializer {
    fun serialize(body: Message): ByteArray
}