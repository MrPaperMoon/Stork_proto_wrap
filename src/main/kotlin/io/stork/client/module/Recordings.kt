package io.stork.client.module

import io.stork.proto.client.recordings.recording.RecordingListRequest
import io.stork.proto.client.recordings.recording.RecordingListResponse
import io.stork.proto.client.recordings.recording.UpdateRecordingTitleRequest
import io.stork.proto.client.recordings.recording.UpdateRecordingTitleResponse

interface Recordings {
    suspend fun list(body: RecordingListRequest): RecordingListResponse // recording.list
    suspend fun updateTitle(body: UpdateRecordingTitleRequest): UpdateRecordingTitleResponse // recording.updateRecordingTitle
}