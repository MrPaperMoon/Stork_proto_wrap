package io.stork.client

import com.google.protobuf.Message
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.stork.client.ktor.DefaultProtobufSerializer
import io.stork.client.ktor.getResult
import io.stork.client.module.*
import io.stork.client.module.Account
import io.stork.client.module.Auth
import io.stork.client.module.Chat
import io.stork.client.module.ChatMessage
import io.stork.client.module.Conference
import io.stork.client.module.RTC
import io.stork.client.module.Session
import io.stork.client.module.Workspace
import io.stork.proto.account.*
import io.stork.proto.auth.*
import io.stork.proto.avatar.AvatarUploadResponse
import io.stork.proto.avatar.SetPrimaryAvatarRequest
import io.stork.proto.avatar.SetPrimaryAvatarResponse
import io.stork.proto.calls.conference.*
import io.stork.proto.calls.rtc.*
import io.stork.proto.chat.*
import io.stork.proto.chat.message.*
import io.stork.proto.client.recordings.recording.RecordingListRequest
import io.stork.proto.client.recordings.recording.RecordingListResponse
import io.stork.proto.client.recordings.recording.UpdateRecordingTitleRequest
import io.stork.proto.client.recordings.recording.UpdateRecordingTitleResponse
import io.stork.proto.files.file.*
import io.stork.proto.member.MemberListRequest
import io.stork.proto.member.MemberListResponse
import io.stork.proto.publicProfile.PublicProfileListRequest
import io.stork.proto.publicProfile.PublicProfileListResponse
import io.stork.proto.session.*
import io.stork.proto.workspace.*
import okhttp3.MultipartBody
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream

internal class KtorApiClient(
    private val config: ApiClientConfig,
    private val client: HttpClient,
    private val sessionManager: SessionManager,
    override val websocket: EventWebsocket
): ApiClient, SessionManager by sessionManager {
    private val log = LoggerFactory.getLogger("ApiClient")

    private fun apiCallUrl(path: String): String = config.apiBaseUrl + "/" + path

    private fun logRequest(url: String, body: Any? = null) {
        when (config.logLevel) {
            LogLevel.NONE -> {}
            LogLevel.BASIC -> log.info("$url <<<")
            LogLevel.BODY -> log.info("$url <<< {}", body ?: "")
        }
    }

    private suspend inline fun <reified T: Any> getAndLogResult(response: HttpResponse): Result<T> {
        val result = response.getResult<T>()
        when (config.logLevel) {
            LogLevel.BASIC -> log.info("{} >>> {}", response.call.request.url, response.status)
            LogLevel.BODY -> log.info("{} >>> {} {}", response.call.request.url, response.status, result)
        }

        return result
    }

    private suspend inline fun <reified T: Any> makeApiCall(path: String, body: Message): T {
        val url = apiCallUrl(path)

        logRequest(url, body)

        val response = client.post<HttpResponse> {
            url(url)
            contentType(ContentType.parse(config.mediaType.contentType))
            this.body = body
        }

        val result = getAndLogResult<T>(response)
        return result.getOrThrow()
    }

    private suspend inline fun <reified T: Any> makeApiCallWithoutBody(path: String, method: HttpMethod = HttpMethod.Get): T {
        val url = apiCallUrl(path)

        logRequest(url)

        val response = client.request<HttpResponse> {
            url(url)
            contentType(ContentType.parse(config.mediaType.contentType))
            this.method = method
        }

        val result = getAndLogResult<T>(response)
        return result.getOrThrow()
    }


    override val account: Account = object: Account {
        override suspend fun list(body: AccountsListRequest): AccountsListResponse =
            makeApiCall("account.list", body)

        override suspend fun updatePassword(body: UpdateAccountPasswordRequest): UpdateAccountPasswordResponse =
            makeApiCall("account.updatePasswords", body)

        override suspend fun updateName(body: UpdateAccountNameRequest): UpdateAccountNameResponse =
            makeApiCall("account.updateName", body)

    }
    override val auth: Auth = object : Auth {
        override suspend fun checkEmail(body: CheckEmailRequest): CheckEmailResponse {
            return makeApiCall("auth.checkEmail", body)
        }

        override suspend fun login(body: LoginRequest): LoginResponse {
            return makeApiCall("auth.login", body)
        }

        override suspend fun sendMagicLink(body: SendMagicLinkRequest): SendMagicLinkResponse {
            return makeApiCall("auth.sendMagicLink", body)
        }

        override suspend fun verifyMagicLinkCode(body: VerifyMagicLinkCodeRequest): LoginResponse {
            return makeApiCall("auth.verifyMagicLinkCode", body)
        }

        override suspend fun verifyMagicLink(body: VerifyMagicLinkRequest): LoginResponse {
            return makeApiCall("auth.verifyMagicLink", body)
        }

        override suspend fun oauthGoogle(body: LoginWithGoogleRequest): LoginResponse {
            return makeApiCall("auth.oauth.google", body)
        }

        override suspend fun oauthSlack(body: LoginWithSlackRequest): LoginResponse {
            return makeApiCall("auth.oauth.slack", body)
        }

    }
    override val avatar: Avatar = object: Avatar {
        override suspend fun uploadFile(file: MultipartBody.Part): AvatarUploadResponse {
            TODO()
        }

        override suspend fun downloadAvatar(avatarId: String, size: AvatarSize, targetFile: File): File {
            return client.get<HttpStatement>("avatar.download/$avatarId/${size.raw}").execute { response: HttpResponse ->
                val downloadChannel = response.receive<ByteReadChannel>()
                FileOutputStream(targetFile).use { fileOutput ->
                    downloadChannel.read {
                        fileOutput.write(it.array())
                    }
                }
                targetFile
            }
        }

        override suspend fun setPrimary(body: SetPrimaryAvatarRequest): SetPrimaryAvatarResponse {
            return makeApiCall("avatar.setPrimary", body)
        }

        override fun getAvatarUrl(avatarId: String, size: AvatarSize): String {
            return "${config.apiBaseUrl}/avatar.download/${avatarId}/${size.raw}"
        }
    }

    override val chat: Chat = object: Chat {
        override suspend fun get(body: GetChatRequest): GetChatResponse {
            return makeApiCall("chat.get", body)
        }

        override suspend fun listRecentChats(body: ListRecentChatsRequest): ListRecentChatsResponse {
            return makeApiCall("chat.recent.list", body)
        }

        override suspend fun create(body: CreateChatRequest): CreateChatResponse {
            return makeApiCall("chat.create", body)
        }

        override suspend fun update(body: UpdateChatRequest): UpdateChatResponse {
            return makeApiCall("chat.update", body)
        }

        override suspend fun join(body: JoinChatRequest): JoinChatResponse {
            return makeApiCall("chat.join", body)
        }

        override suspend fun leave(body: LeaveChatRequest): LeaveChatResponse {
            return makeApiCall("chat.leave", body)
        }

        override suspend fun archive(body: ArchiveChatRequest): ArchiveChatResponse {
            return makeApiCall("chat.archive", body)
        }

        override suspend fun markAsRead(body: MarkChatAsReadRequest): MarkChatAsReadResponse {
            return makeApiCall("chat.markAsRead", body)
        }

        override suspend fun search(body: SearchChatRequest): SearchChatResponse {
            return makeApiCall("chat.search", body)
        }
    }
    override val chatMessage: ChatMessage = object: ChatMessage {
        override suspend fun get(body: GetChatMessagesRequest): GetChatMessagesResponse {
            return makeApiCall("chat.message.get", body)
        }

        override suspend fun send(body: SendChatMessageRequest): SendChatMessageResponse {
            return makeApiCall("chat.message.send", body)
        }

        override suspend fun edit(body: EditChatMessageRequest): EditChatMessageResponse {
            return makeApiCall("chat.message.edit", body)
        }

        override suspend fun toggleReaction(body: ToggleChatMessageReactionRequest): ToggleChatMessageReactionResponse {
            return makeApiCall("chat.message.toggleReaction", body)
        }

    }
    override val conference: Conference = object: Conference {
        override suspend fun create(body: CreateConferenceRequest): CreateConferenceResponse {
            return makeApiCall("conference.create", body)
        }

        override suspend fun join(body: JoinConferenceRequest): JoinConferenceResponse {
            return makeApiCall("conference.join", body)
        }

        override suspend fun list(body: ConferenceListRequest): ConferenceListResponse {
            return makeApiCall("conference.list", body)
        }

        override suspend fun leave(body: LeaveConferenceRequest): LeaveConferenceResponse {
            return makeApiCall("conference.leave", body)
        }

        override suspend fun inviteToConference(body: InviteToConferenceRequest): InviteToConferenceResponse {
            return makeApiCall("conference.invite", body)
        }

        override suspend fun watercoolerUpdateScope(body: ConferenceWatercoolerUpdateScopeRequest): ConferenceWatercoolerUpdateScopeResponse {
            return makeApiCall("conference.watercooler.updateScope", body)
        }

        override suspend fun conferenceInfo(body: ConferenceInfoRequest): ConferenceInfoResponse {
            return makeApiCall("conference.info", body)
        }

        override suspend fun conferenceVoiceChannelUpdateMute(body: ConferenceVoiceChannelUpdateMuteRequest): ConferenceVoiceChannelUpdateMuteResponse {
            return makeApiCall("conference.voiceChannel.updateMute", body)
        }

    }

    override val file: io.stork.client.module.File = object: io.stork.client.module.File {
        override suspend fun getPreSignedUrl(body: GetFilePreSignedUrlRequest): GetFilePreSignedUrlResponse {
            return makeApiCall("file.getPreSignedUrl", body)
        }

        override suspend fun startMultipart(body: UploadFileRequest): StartMultipartFileUploadResponse {
            return makeApiCall("file.startMultipart", body)
        }

        override suspend fun finishPart(body: FinishPartUploadRequest): FinishPartUploadResponse {
            return makeApiCall("file.finishPart", body)
        }

        override suspend fun finishMultipart(body: FinishMultipartFileUploadRequest): FinishMultipartFileUploadResponse {
            return makeApiCall("file.finishMultipart", body)
        }

        override suspend fun uploadFile(
            body: UploadFileRequest,
            content: File
        ): UploadFileResponse {
            val url = apiCallUrl("file.directUpload")
            logRequest(url)
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = url,
                formData = formData {
                    append("uploadFileRequest", DefaultProtobufSerializer.write(body).bytes())
                    append("content", content.readBytes(), Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=${content.name}")
                    })
                }
            )
            return getAndLogResult<UploadFileResponse>(response).getOrThrow()
        }

        override suspend fun getFileMetadata(fileId: String): GetFileMetadataResponse {
            return makeApiCallWithoutBody("file.metadata/$fileId", HttpMethod.Get)
        }

        override suspend fun deleteFile(fileId: String) {
            return makeApiCallWithoutBody("file.delete/$fileId", HttpMethod.Delete)
        }
    }

    override val member: Member = object: Member {
        override suspend fun list(body: MemberListRequest): MemberListResponse {
            return makeApiCall("member.list", body)
        }
    }

    override val publicProfile: PublicProfile = object: PublicProfile {
        override suspend fun list(body: PublicProfileListRequest): PublicProfileListResponse {
            return makeApiCall("publicProfile.list", body)
        }
    }

    override val recordings: Recordings = object: Recordings {
        override suspend fun list(body: RecordingListRequest): RecordingListResponse {
            return makeApiCall("recording.list", body)
        }

        override suspend fun updateTitle(body: UpdateRecordingTitleRequest): UpdateRecordingTitleResponse {
            return makeApiCall("recording.updateRecordingTitle", body)
        }
    }

    override val rtc: RTC = object: RTC {
        override suspend fun negotiateConnection(body: RTCConnectionNegotiateRequest): RTCConnectionNegotiateResponse {
            return makeApiCall("rtc.negotiate", body)
        }

        override suspend fun addIceCandidates(body: AddRTCIceCandidatesRequest): AddRTCIceCandidatesResponse {
            return makeApiCall("rtc.addIceCandidates", body)
        }

        override suspend fun removeIceCandidates(body: RemoveRTCIceCandidatesRequest): RemoveRTCIceCandidatesResponse {
            return makeApiCall("rtc.removeIceCandidates", body)
        }
    }

    override val session: Session = object: Session {
        override suspend fun generate(body: GenerateSessionRequest): GenerateSessionResponse {
            return makeApiCall("session.generate", body)
        }

        override suspend fun logout(body: LogoutRequest): LogoutResponse {
            return makeApiCall("session.logout", body)
        }

        override suspend fun addPushNotificationsToken(body: AddPushNotificationsTokenRequest): AddPushNotificationsTokenResponse {
            return makeApiCall("session.addPushNotificationsToken", body)
        }

        override suspend fun removePushNotificationsToken(body: RemovePushNotificationsTokenRequest): RemovePushNotificationsTokenResponse {
            return makeApiCall("session.removePushNotificationsToken", body)
        }
    }

    override val workspace: Workspace = object: Workspace {
        override suspend fun join(body: JoinWorkspaceRequest): JoinWorkspaceResponse {
            return makeApiCall("workspace.join", body)
        }

        override suspend fun invite(body: InviteToWorkspaceRequest): InviteToWorkspaceResponse {
            return makeApiCall("workspace.invite", body)
        }

        override suspend fun importMembersFromSlack(body: ImportMembersToWorkspaceFromSlackRequest): ImportMembersToWorkspaceFromSlackResponse {
            return makeApiCall("workspace.importMembers.slack", body)
        }

        override suspend fun importMembersFromGoogle(body: ImportMembersToWorkspaceFromGoogleRequest): ImportMembersToWorkspaceFromGoogleResponse {
            return makeApiCall("workspace.importMembers.google", body)
        }

        override suspend fun list(body: WorkspaceListRequest): WorkspaceListResponse {
            return makeApiCall("workspace.list", body)
        }

        override suspend fun create(body: CreateWorkspaceRequest): CreateWorkspaceResponse {
            return makeApiCall("workspace.create", body)
        }

        override suspend fun checkSubdomain(body: CheckWorkspaceSubdomainRequest): CheckWorkspaceSubdomainResponse {
            return makeApiCall("workspace.checkSubdomain", body)
        }
    }
}