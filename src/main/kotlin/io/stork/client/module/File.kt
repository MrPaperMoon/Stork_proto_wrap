package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.file.*

interface File {
    fun getFileUrl(fileId: String): String

    suspend fun getPreSignedUrl(body: GetFilePreSignedUrlRequest): ApiResult<GetFilePreSignedUrlResponse>

    suspend fun startMultipart(body: UploadFileRequest): ApiResult<StartMultipartFileUploadResponse>

    suspend fun finishPart(body: FinishPartUploadRequest): ApiResult<FinishPartUploadResponse>

    suspend fun finishMultipart(body: FinishMultipartFileUploadRequest): ApiResult<FinishMultipartFileUploadResponse>

    suspend fun uploadFile(body: UploadFileRequest, content: java.io.File): ApiResult<UploadFileResponse>

    suspend fun getFileMetadata(fileId: String): ApiResult<GetFileMetadataResponse>

    suspend fun deleteFile(fileId: String)
}
