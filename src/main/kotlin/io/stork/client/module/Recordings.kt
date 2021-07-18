package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.recordings.recording.RecordingListRequest
import io.stork.proto.client.recordings.recording.RecordingListResponse
import io.stork.proto.client.recordings.recording.UpdateRecordingTitleRequest
import io.stork.proto.client.recordings.recording.UpdateRecordingTitleResponse

interface Recordings {
    suspend fun list(body: RecordingListRequest): ApiResult<RecordingListResponse>
    suspend fun updateTitle(body: UpdateRecordingTitleRequest): ApiResult<UpdateRecordingTitleResponse>
}