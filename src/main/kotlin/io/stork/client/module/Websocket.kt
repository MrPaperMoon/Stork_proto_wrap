package io.stork.client.module

import io.stork.proto.calls.conference.ConferenceEvent
import io.stork.proto.calls.rtc.RTCEvent
import io.stork.proto.chat.ChatEvent
import io.stork.proto.client.recordings.recording.RecordingEvent
import io.stork.proto.client.workspace.WorkspaceEvent
import io.stork.proto.files.file.FileEvent
import io.stork.proto.member.MemberEvent
import io.stork.proto.notification.Notification
import io.stork.proto.profile.ProfileEvent
import io.stork.proto.websocket.Echo
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

interface Websocket {
    suspend fun sendEcho(echo: Echo)
    val receiveEcho: Flow<Echo>

    val notifications: Flow<Notification>

    val webRTCEvents: Flow<RTCEvent>
        get() = notifications.getEvents {
            it.rtc_event
        }
    val conferenceEvents: Flow<ConferenceEvent>
        get() = notifications.getEvents {
            it.conference_event
        }

    val memberEvents: Flow<MemberEvent>
        get() = notifications.getEvents {
            it.member_event
        }

    val profileEvents: Flow<ProfileEvent>
        get() = notifications.getEvents {
            it.profile_event
        }

    val workspaceEvents: Flow<WorkspaceEvent>
        get() = notifications.getEvents {
            it.workspace_event
        }

    val recordingEvents: Flow<RecordingEvent>
        get() = notifications.getEvents {
            it.recording_event
        }

    val chatEvents: Flow<ChatEvent>
        get() = notifications.getEvents {
            it.chat_event
        }

    val fileEvents: Flow<FileEvent>
        get() = notifications.getEvents {
            it.file_event
        }

    private fun <T: Any> Flow<Notification>.getEvents(selector: (Notification) -> T?): Flow<T> = mapNotNull {
        selector(it)
    }
}