package io.stork.client.websocket

import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.stork.proto.calls.conference.ConferenceEvent
import io.stork.proto.calls.rtc.RTCEvent
import io.stork.proto.websocket.EchoMessage
import kotlinx.coroutines.channels.ReceiveChannel

interface EventWebsocket {
    @Receive
    fun conferenceEvent(): ReceiveChannel<ConferenceEvent>
    @Receive
    fun webRTCEvent(): ReceiveChannel<RTCEvent>

    @Send
    fun sendEcho(echo: EchoMessage): Boolean
    @Receive
    fun receiveEcho(): ReceiveChannel<EchoMessage>
}