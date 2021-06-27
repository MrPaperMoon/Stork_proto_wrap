package io.stork.client.module

import io.stork.proto.calls.conference.ConferenceEvent
import io.stork.proto.calls.rtc.RTCEvent
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent
import kotlinx.coroutines.flow.Flow

interface EventWebsocket {

    suspend fun sendEcho(echo: EchoMessage): Boolean
    val receiveEcho: Flow<EchoMessage>

    val allEvents: Flow<WebsocketEvent>
    val webRTCEvents: Flow<RTCEvent>
    val conferenceEvents: Flow<ConferenceEvent>
}