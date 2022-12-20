package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.file.FileTranscribeListRequest
import io.stork.proto.client.file.FileTranscribeListResponse
import io.stork.proto.client.file.FinishMultipartFileUploadRequest
import io.stork.proto.client.file.FinishMultipartFileUploadResponse
import io.stork.proto.client.file.FinishPartUploadRequest
import io.stork.proto.client.file.FinishPartUploadResponse
import io.stork.proto.client.file.GetFileMetadataResponse
import io.stork.proto.client.file.GetFilePreSignedUrlRequest
import io.stork.proto.client.file.GetFilePreSignedUrlResponse
import io.stork.proto.client.file.StartMultipartFileUploadResponse
import io.stork.proto.client.file.TranscribeResult
import io.stork.proto.client.file.UploadFileRequest
import io.stork.proto.client.file.UploadFileResponse

interface File {
    fun getFileUrl(fileId: String): String

    suspend fun getPreSignedUrl(body: GetFilePreSignedUrlRequest): ApiResult<GetFilePreSignedUrlResponse>
    suspend fun startMultipart(body: UploadFileRequest): ApiResult<StartMultipartFileUploadResponse>
    suspend fun finishPart(body: FinishPartUploadRequest): ApiResult<FinishPartUploadResponse>
    suspend fun finishMultipart(body: FinishMultipartFileUploadRequest): ApiResult<FinishMultipartFileUploadResponse>
    suspend fun uploadFile(body: UploadFileRequest,
                           content: java.io.File): ApiResult<UploadFileResponse>

    suspend fun getFileMetadata(fileId: String): ApiResult<GetFileMetadataResponse>
    suspend fun deleteFile(fileId: String)
    suspend fun getTranscribe(fileId: String): ApiResult<TranscribeResult>
    suspend fun getTranscribeList(body: FileTranscribeListRequest): ApiResult<FileTranscribeListResponse>
}
