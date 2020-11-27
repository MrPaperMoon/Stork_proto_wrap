package io.stork.client.retrofit.module

import io.stork.proto.avatar.AvatarUploadResponse
import io.stork.proto.avatar.SetPrimaryAvatarRequest
import io.stork.proto.avatar.SetPrimaryAvatarResponse
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.io.File


interface Avatar {
    @POST("avatar.upload")
    fun uploadFile(@Part file: MultipartBody.Part): Call<AvatarUploadResponse>

    @GET("avatar.download/{avatar_id}/{size}")
    fun downloadAvatar(@Path("avatar_id") avatarId: String, size: Int): Call<ResponseBody>

    @POST("avatar.setPrimary")
    fun setPrimary(@Body body: SetPrimaryAvatarRequest): Call<SetPrimaryAvatarResponse>
}


private val imageMediaType = "image/*".toMediaTypeOrNull()
fun filePart(file: File): MultipartBody.Part =
        MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(imageMediaType))

fun Avatar.uploadFile(file: File): Call<AvatarUploadResponse> = uploadFile(filePart(file))