package io.stork.client.ktor.ws

import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent

sealed interface WSPacket {
    data class Event(val event: WebsocketEvent): WSPacket
    data class Echo(val echoMessage: EchoMessage): WSPacket
}