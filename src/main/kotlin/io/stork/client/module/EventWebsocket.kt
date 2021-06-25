package io.stork.client.module

import io.stork.proto.calls.conference.ConferenceEvent
import io.stork.proto.calls.rtc.RTCEvent
import io.stork.proto.websocket.EchoMessage
import kotlinx.coroutines.flow.Flow

interface EventWebsocket {
    fun conferenceEvent(): Flow<ConferenceEvent>
    fun webRTCEvent(): Flow<RTCEvent>

    suspend fun sendEcho(echo: EchoMessage): Boolean
    fun receiveEcho(): Flow<EchoMessage>
}