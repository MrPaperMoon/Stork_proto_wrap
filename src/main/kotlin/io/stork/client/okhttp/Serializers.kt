package io.stork.client.okhttp

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.wire.WireTypeAdapterFactory
import io.stork.client.ktor.DefaultProtobufSerializer
import io.stork.client.ktor.ProtobufSerializer

class Serializers {
    val protobufSerializer: ProtobufSerializer = DefaultProtobufSerializer
    val gson: Gson by lazy {
        GsonBuilder().apply { configureGson() }.create()
    }

    fun GsonBuilder.configureGson() {
        registerTypeAdapterFactory(WireTypeAdapterFactory())
    }
}