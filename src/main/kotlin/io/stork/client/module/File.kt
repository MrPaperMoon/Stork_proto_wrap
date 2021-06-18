package io.stork.client.module

import io.stork.proto.files.file.*

interface File {
    suspend fun getPreSignedUrl(body: GetFilePreSignedUrlRequest): GetFilePreSignedUrlResponse

    suspend fun startMultipart(body: UploadFileRequest): StartMultipartFileUploadResponse

    suspend fun finishPart(body: FinishPartUploadRequest): FinishPartUploadResponse

    suspend fun finishMultipart(body: FinishMultipartFileUploadRequest): FinishMultipartFileUploadResponse

    suspend fun uploadFile(body: UploadFileRequest, content: java.io.File): UploadFileResponse

    suspend fun getFileMetadata(fileId: String): GetFileMetadataResponse

    suspend fun deleteFile(fileId: String)
}