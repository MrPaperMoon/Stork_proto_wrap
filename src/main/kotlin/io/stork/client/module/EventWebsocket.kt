package io.stork.client.module

import io.stork.proto.calls.conference.ConferenceEvent
import io.stork.proto.calls.rtc.RTCEvent
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

interface EventWebsocket {
    suspend fun sendEcho(echo: EchoMessage): Boolean
    val receiveEcho: Flow<EchoMessage>

    val allEvents: Flow<WebsocketEvent>
    val webRTCEvents: Flow<RTCEvent>
        get() = allEvents.getEvents(WebsocketEvent.EventCase.RTC_EVENT) {
            it.rtcEvent
        }
    val conferenceEvents: Flow<ConferenceEvent>
        get() = allEvents.getEvents(WebsocketEvent.EventCase.CONFERENCE_EVENT) {
            it.conferenceEvent
        }
}

fun <T> Flow<WebsocketEvent>.getEvents(type: WebsocketEvent.EventCase, selector: (WebsocketEvent) -> T): Flow<T> {
    return mapNotNull {
        if (it.eventCase == type) {
            selector(it)
        } else null
    }
}