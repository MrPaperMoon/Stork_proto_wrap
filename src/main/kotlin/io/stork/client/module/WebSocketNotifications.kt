package io.stork.client.module

import io.stork.client.WebSocket
import io.stork.proto.client.calls.conference.ConferenceEvent
import io.stork.proto.client.calls.rtc.RTCEvent
import io.stork.proto.client.file.FileEvent
import io.stork.proto.client.member.MemberEvent
import io.stork.proto.client.messaging.chat.ChatEvent
import io.stork.proto.client.profiles.ProfileEvent
import io.stork.proto.client.recording.RecordingEvent
import io.stork.proto.client.workspace.WorkspaceEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull


val WebSocket.webRTCEvents: Flow<RTCEvent>
    get() = notifications.mapNotNull {
        it.rtc_event
    }
val WebSocket.conferenceEvents: Flow<ConferenceEvent>
    get() = notifications.mapNotNull {
        it.conference_event
    }

val WebSocket.memberEvents: Flow<MemberEvent>
    get() = notifications.mapNotNull {
        it.member_event
    }

val WebSocket.profileEvents: Flow<ProfileEvent>
    get() = notifications.mapNotNull {
        it.profile_event
    }

val WebSocket.workspaceEvents: Flow<WorkspaceEvent>
    get() = notifications.mapNotNull {
        it.workspace_event
    }

val WebSocket.recordingEvents: Flow<RecordingEvent>
    get() = notifications.mapNotNull {
        it.recording_event
    }

val WebSocket.chatEvents: Flow<ChatEvent>
    get() = notifications.mapNotNull {
        it.chat_event
    }

val WebSocket.fileEvents: Flow<FileEvent>
    get() = notifications.mapNotNull {
        it.file_event
    }
