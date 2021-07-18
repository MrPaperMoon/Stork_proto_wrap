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
        get() = allEvents.getEvents {
            it.rtc_event
        }
    val conferenceEvents: Flow<ConferenceEvent>
        get() = allEvents.getEvents {
            it.conference_event
        }

    val memberEvents: Flow<MemberEvent>
        get() = allEvents.getEvents {
            it.member_event
        }

    val profileEvents: Flow<ProfileEvent>
        get() = allEvents.getEvents {
            it.profile_event
        }

    val workspaceEvents: Flow<WorkspaceEvent>
        get() = allEvents.getEvents {
            it.workspace_event
        }

    val recordingEvents: Flow<RecordingEvent>
        get() = allEvents.getEvents {
            it.recording_event
        }

    val chatEvents: Flow<ChatEvent>
        get() = allEvents.getEvents {
            it.chat_event
        }

    val fileEvents: Flow<FileEvent>
        get() = allEvents.getEvents {
            it.file_event
        }

    private fun <T: Any> Flow<WebsocketEvent>.getEvents(selector: (WebsocketEvent) -> T?): Flow<T> = mapNotNull {
        selector(it)
    }
}