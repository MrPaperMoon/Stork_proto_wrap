package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.recording.RecordingListRequest
import io.stork.proto.client.recording.RecordingListResponse
import io.stork.proto.client.recording.RemoveRecordingRequest
import io.stork.proto.client.recording.RemoveRecordingResponse
import io.stork.proto.client.recording.UpdateRecordingTitleRequest
import io.stork.proto.client.recording.UpdateRecordingTitleResponse

interface Recordings {
    suspend fun list(body: RecordingListRequest): ApiResult<RecordingListResponse>
    suspend fun updateTitle(body: UpdateRecordingTitleRequest): ApiResult<UpdateRecordingTitleResponse>
    suspend fun remove(body: RemoveRecordingRequest): ApiResult<RemoveRecordingResponse>
}
