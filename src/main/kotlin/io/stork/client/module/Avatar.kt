package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.client.AvatarSize
import io.stork.client.BinaryContent
import io.stork.client.FileBinaryContent
import io.stork.client.UploadStatusCallback
import io.stork.proto.client.avatar.AvatarUploadResponse
import io.stork.proto.client.avatar.SetPrimaryAvatarRequest
import io.stork.proto.client.avatar.SetPrimaryAvatarResponse
import java.io.File


interface Avatar {
    suspend fun uploadFile(file: BinaryContent,
                           uploadStatusCallback: UploadStatusCallback? = null): ApiResult<AvatarUploadResponse>

    suspend fun downloadAvatar(avatarId: String, size: AvatarSize, targetFile: File): File

    suspend fun setPrimary(body: SetPrimaryAvatarRequest): ApiResult<SetPrimaryAvatarResponse>

    fun getAvatarUrl(avatarId: String, size: AvatarSize): String
}

// convenience method
suspend fun Avatar.uploadFile(file: File,
                              uploadStatusCallback: UploadStatusCallback? = null): ApiResult<AvatarUploadResponse> {
    return uploadFile(FileBinaryContent(file), uploadStatusCallback)
}
