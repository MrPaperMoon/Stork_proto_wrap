package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.client.AvatarSize
import io.stork.proto.avatar.AvatarUploadResponse
import io.stork.proto.avatar.SetPrimaryAvatarRequest
import io.stork.proto.avatar.SetPrimaryAvatarResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


interface Avatar {
    suspend fun uploadFile(file: MultipartBody.Part): ApiResult<AvatarUploadResponse>

    suspend fun downloadAvatar(avatarId: String, size: AvatarSize, targetFile: File): File

    suspend fun setPrimary(body:  SetPrimaryAvatarRequest): ApiResult<SetPrimaryAvatarResponse>

    fun getAvatarUrl(avatarId: String, size: AvatarSize): String
}


private val imageMediaType = "image/*".toMediaTypeOrNull()
fun filePart(file: File): MultipartBody.Part =
        MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(imageMediaType))

suspend fun Avatar.uploadFile(file: File): ApiResult<AvatarUploadResponse> = uploadFile(filePart(file))