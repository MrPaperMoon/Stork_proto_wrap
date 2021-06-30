package io.stork.client.module

import io.stork.proto.calls.conference.ConferenceEvent
import io.stork.proto.calls.rtc.RTCEvent
import io.stork.proto.chat.ChatEvent
import io.stork.proto.client.recordings.recording.RecordingEvent
import io.stork.proto.client.workspace.WorkspaceEvent
import io.stork.proto.files.file.FileEvent
import io.stork.proto.member.MemberEvent
import io.stork.proto.profile.ProfileEvent
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

    val memberEvents: Flow<MemberEvent>
        get() = allEvents.getEvents(WebsocketEvent.EventCase.MEMBER_EVENT) {
            it.memberEvent
        }

    val profileEvents: Flow<ProfileEvent>
        get() = allEvents.getEvents(WebsocketEvent.EventCase.PROFILE_EVENT) {
            it.profileEvent
        }

    val workspaceEvents: Flow<WorkspaceEvent>
        get() = allEvents.getEvents(WebsocketEvent.EventCase.WORKSPACE_EVENT) {
            it.workspaceEvent
        }

    val recordingEvents: Flow<RecordingEvent>
        get() = allEvents.getEvents(WebsocketEvent.EventCase.RECORDING_EVENT) {
            it.recordingEvent
        }

    val chatEvents: Flow<ChatEvent>
        get() = allEvents.getEvents(WebsocketEvent.EventCase.CHAT_EVENT) {
            it.chatEvent
        }

    val fileEvents: Flow<FileEvent>
        get() = allEvents.getEvents(WebsocketEvent.EventCase.FILE_EVENT) {
            it.fileEvent
        }
}

fun <T> Flow<WebsocketEvent>.getEvents(type: WebsocketEvent.EventCase, selector: (WebsocketEvent) -> T): Flow<T> {
    return mapNotNull {
        if (it.eventCase == type) {
            selector(it)
        } else null
    }
}