package io.stork.client.module

import io.stork.client.ApiNotificationsSource
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


val ApiNotificationsSource.webRTCEvents: Flow<RTCEvent>
    get() = notifications.mapNotNull {
        it.rtc_event
    }
val ApiNotificationsSource.conferenceEvents: Flow<ConferenceEvent>
    get() = notifications.mapNotNull {
        it.conference_event
    }

val ApiNotificationsSource.memberEvents: Flow<MemberEvent>
    get() = notifications.mapNotNull {
        it.member_event
    }

val ApiNotificationsSource.profileEvents: Flow<ProfileEvent>
    get() = notifications.mapNotNull {
        it.profile_event
    }

val ApiNotificationsSource.workspaceEvents: Flow<WorkspaceEvent>
    get() = notifications.mapNotNull {
        it.workspace_event
    }

val ApiNotificationsSource.recordingEvents: Flow<RecordingEvent>
    get() = notifications.mapNotNull {
        it.recording_event
    }

val ApiNotificationsSource.chatEvents: Flow<ChatEvent>
    get() = notifications.mapNotNull {
        it.chat_event
    }

val ApiNotificationsSource.fileEvents: Flow<FileEvent>
    get() = notifications.mapNotNull {
        it.file_event
    }
