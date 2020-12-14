package io.stork.client.module

import io.stork.proto.avatar.AvatarUploadResponse
import io.stork.proto.avatar.SetPrimaryAvatarRequest
import io.stork.proto.avatar.SetPrimaryAvatarResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


interface Avatar {
    suspend fun uploadFile(file: MultipartBody.Part): AvatarUploadResponse

    suspend fun downloadAvatar(avatarId: String, size: Int, targetFile: File): File

    suspend fun setPrimary(body:  SetPrimaryAvatarRequest): SetPrimaryAvatarResponse
}


private val imageMediaType = "image/*".toMediaTypeOrNull()
fun filePart(file: File): MultipartBody.Part =
        MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(imageMediaType))

suspend fun Avatar.uploadFile(file: File): AvatarUploadResponse = uploadFile(filePart(file))