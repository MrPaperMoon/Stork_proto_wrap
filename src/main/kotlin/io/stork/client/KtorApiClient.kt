package io.stork.client

import com.squareup.wire.AnyMessage
import com.squareup.wire.Message
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import io.stork.client.ktor.DefaultProtobufSerializer
import io.stork.client.ktor.getResult
import io.stork.client.module.Account
import io.stork.client.module.Auth
import io.stork.client.module.Avatar
import io.stork.client.module.Chat
import io.stork.client.module.ChatActivity
import io.stork.client.module.ChatMessage
import io.stork.client.module.Conference
import io.stork.client.module.Member
import io.stork.client.module.PublicProfile
import io.stork.client.module.RTC
import io.stork.client.module.Recordings
import io.stork.client.module.Session
import io.stork.client.module.Workspace
import io.stork.client.ws.WebSocketProvider
import io.stork.proto.client.account.AccountsListRequest
import io.stork.proto.client.account.AccountsListResponse
import io.stork.proto.client.account.UpdateAccountNameRequest
import io.stork.proto.client.account.UpdateAccountNameResponse
import io.stork.proto.client.account.UpdateAccountPasswordRequest
import io.stork.proto.client.account.UpdateAccountPasswordResponse
import io.stork.proto.client.auth.CheckEmailRequest
import io.stork.proto.client.auth.CheckEmailResponse
import io.stork.proto.client.auth.LoginRequest
import io.stork.proto.client.auth.LoginResponse
import io.stork.proto.client.auth.LoginWithGoogleRequest
import io.stork.proto.client.auth.LoginWithSlackRequest
import io.stork.proto.client.auth.SendMagicLinkRequest
import io.stork.proto.client.auth.SendMagicLinkResponse
import io.stork.proto.client.auth.VerifyMagicLinkCodeRequest
import io.stork.proto.client.auth.VerifyMagicLinkRequest
import io.stork.proto.client.avatar.AvatarUploadResponse
import io.stork.proto.client.avatar.SetPrimaryAvatarRequest
import io.stork.proto.client.avatar.SetPrimaryAvatarResponse
import io.stork.proto.client.calls.conference.ConferenceInfoRequest
import io.stork.proto.client.calls.conference.ConferenceInfoResponse
import io.stork.proto.client.calls.conference.ConferenceListRequest
import io.stork.proto.client.calls.conference.ConferenceListResponse
import io.stork.proto.client.calls.conference.ConferenceVoiceChannelUpdateMuteRequest
import io.stork.proto.client.calls.conference.ConferenceVoiceChannelUpdateMuteResponse
import io.stork.proto.client.calls.conference.ConferenceWatercoolerUpdateScopeRequest
import io.stork.proto.client.calls.conference.ConferenceWatercoolerUpdateScopeResponse
import io.stork.proto.client.calls.conference.CreateConferenceRequest
import io.stork.proto.client.calls.conference.CreateConferenceResponse
import io.stork.proto.client.calls.conference.InviteToConferenceRequest
import io.stork.proto.client.calls.conference.InviteToConferenceResponse
import io.stork.proto.client.calls.conference.JoinConferenceRequest
import io.stork.proto.client.calls.conference.JoinConferenceResponse
import io.stork.proto.client.calls.conference.LeaveConferenceRequest
import io.stork.proto.client.calls.conference.LeaveConferenceResponse
import io.stork.proto.client.calls.rtc.AddRTCIceCandidatesRequest
import io.stork.proto.client.calls.rtc.AddRTCIceCandidatesResponse
import io.stork.proto.client.calls.rtc.RTCConnectionNegotiateRequest
import io.stork.proto.client.calls.rtc.RTCConnectionNegotiateResponse
import io.stork.proto.client.calls.rtc.RemoveRTCIceCandidatesRequest
import io.stork.proto.client.calls.rtc.RemoveRTCIceCandidatesResponse
import io.stork.proto.client.file.FinishMultipartFileUploadRequest
import io.stork.proto.client.file.FinishMultipartFileUploadResponse
import io.stork.proto.client.file.FinishPartUploadRequest
import io.stork.proto.client.file.FinishPartUploadResponse
import io.stork.proto.client.file.GetFileMetadataResponse
import io.stork.proto.client.file.GetFilePreSignedUrlRequest
import io.stork.proto.client.file.GetFilePreSignedUrlResponse
import io.stork.proto.client.file.StartMultipartFileUploadResponse
import io.stork.proto.client.file.UploadFileRequest
import io.stork.proto.client.file.UploadFileResponse
import io.stork.proto.client.member.MemberListRequest
import io.stork.proto.client.member.MemberListResponse
import io.stork.proto.client.messaging.chat.ArchiveChatRequest
import io.stork.proto.client.messaging.chat.ArchiveChatResponse
import io.stork.proto.client.messaging.chat.CreateChatRequest
import io.stork.proto.client.messaging.chat.CreateChatResponse
import io.stork.proto.client.messaging.chat.EditChatMessageRequest
import io.stork.proto.client.messaging.chat.EditChatMessageResponse
import io.stork.proto.client.messaging.chat.GetChatMessagesRequest
import io.stork.proto.client.messaging.chat.GetChatMessagesResponse
import io.stork.proto.client.messaging.chat.GetChatRequest
import io.stork.proto.client.messaging.chat.GetChatResponse
import io.stork.proto.client.messaging.chat.JoinChatRequest
import io.stork.proto.client.messaging.chat.JoinChatResponse
import io.stork.proto.client.messaging.chat.LeaveChatRequest
import io.stork.proto.client.messaging.chat.LeaveChatResponse
import io.stork.proto.client.messaging.chat.ListRecentChatsRequest
import io.stork.proto.client.messaging.chat.ListRecentChatsResponse
import io.stork.proto.client.messaging.chat.MarkChatAsReadRequest
import io.stork.proto.client.messaging.chat.MarkChatAsReadResponse
import io.stork.proto.client.messaging.chat.RemoveChatMessageRequest
import io.stork.proto.client.messaging.chat.RemoveChatMessageResponse
import io.stork.proto.client.messaging.chat.SearchChatRequest
import io.stork.proto.client.messaging.chat.SearchChatResponse
import io.stork.proto.client.messaging.chat.SendChatMessageRequest
import io.stork.proto.client.messaging.chat.SendChatMessageResponse
import io.stork.proto.client.messaging.chat.StartChatActivityRequest
import io.stork.proto.client.messaging.chat.StartChatActivityResponse
import io.stork.proto.client.messaging.chat.StopChatActivityRequest
import io.stork.proto.client.messaging.chat.StopChatActivityResponse
import io.stork.proto.client.messaging.chat.ToggleChatMessageReactionRequest
import io.stork.proto.client.messaging.chat.ToggleChatMessageReactionResponse
import io.stork.proto.client.messaging.chat.UpdateChatRequest
import io.stork.proto.client.messaging.chat.UpdateChatResponse
import io.stork.proto.client.profiles.PublicProfileListRequest
import io.stork.proto.client.profiles.PublicProfileListResponse
import io.stork.proto.client.recording.RecordingListRequest
import io.stork.proto.client.recording.RecordingListResponse
import io.stork.proto.client.recording.UpdateRecordingTitleRequest
import io.stork.proto.client.recording.UpdateRecordingTitleResponse
import io.stork.proto.client.session.AddPushNotificationsTokenRequest
import io.stork.proto.client.session.AddPushNotificationsTokenResponse
import io.stork.proto.client.session.GenerateSessionRequest
import io.stork.proto.client.session.GenerateSessionResponse
import io.stork.proto.client.session.LogoutRequest
import io.stork.proto.client.session.LogoutResponse
import io.stork.proto.client.session.RemovePushNotificationsTokenRequest
import io.stork.proto.client.session.RemovePushNotificationsTokenResponse
import io.stork.proto.client.session.UpdateClientSystemInfoRequest
import io.stork.proto.client.session.UpdateClientSystemInfoResponse
import io.stork.proto.client.session.UpdateTimezoneRequest
import io.stork.proto.client.session.UpdateTimezoneResponse
import io.stork.proto.client.workspace.CheckWorkspaceSubdomainRequest
import io.stork.proto.client.workspace.CheckWorkspaceSubdomainResponse
import io.stork.proto.client.workspace.CreateWorkspaceRequest
import io.stork.proto.client.workspace.CreateWorkspaceResponse
import io.stork.proto.client.workspace.ImportMembersToWorkspaceFromGoogleRequest
import io.stork.proto.client.workspace.ImportMembersToWorkspaceFromGoogleResponse
import io.stork.proto.client.workspace.ImportMembersToWorkspaceFromSlackRequest
import io.stork.proto.client.workspace.ImportMembersToWorkspaceFromSlackResponse
import io.stork.proto.client.workspace.InviteToWorkspaceRequest
import io.stork.proto.client.workspace.InviteToWorkspaceResponse
import io.stork.proto.client.workspace.JoinWorkspaceRequest
import io.stork.proto.client.workspace.JoinWorkspaceResponse
import io.stork.proto.client.workspace.LeaveWorkspaceRequest
import io.stork.proto.client.workspace.LeaveWorkspaceResponse
import io.stork.proto.client.workspace.UpdateWorkspaceDisplayNameRequest
import io.stork.proto.client.workspace.UpdateWorkspaceDisplayNameResponse
import io.stork.proto.client.workspace.WorkspaceListRequest
import io.stork.proto.client.workspace.WorkspaceListResponse
import java.io.File
import java.io.FileOutputStream
import org.slf4j.LoggerFactory

internal class KtorApiClient(
    private val config: ApiClientConfig,
    private val client: HttpClient,
    private val sessionProvider: SessionProvider,
    private val webSocketProvider: WebSocketProvider
) : ApiClient, SessionProvider by sessionProvider, WebSocketProvider by webSocketProvider {
    private val log = LoggerFactory.getLogger("ApiClient")

    private fun apiCallUrl(path: String): String = config.apiBaseUrl + "/" + path

    private fun logRequest(url: String, body: Any? = null) {
        when (config.logLevel) {
            LogLevel.NONE -> {}
            LogLevel.BASIC -> log.info("$url <<<")
            LogLevel.BODY -> log.info("$url <<< {}", body ?: "")
        }
    }

    private suspend inline fun <reified T : Any> getAndLogResult(response: HttpResponse): ApiResult<T> {
        val result = response.getResult<T>()
        when (config.logLevel) {
            LogLevel.BASIC -> log.info("{} >>> {}", response.call.request.url, response.status)
            LogLevel.BODY -> log.info("{} >>> {} {}", response.call.request.url, response.status, result)
            else -> {}
        }

        return result
    }

    private suspend inline fun <reified T : Any> makeMultipartApiCall(
        path: String,
        formData: List<PartData>,
        noinline uploadStatusCallback: UploadStatusCallback? = null
    ): ApiResult<T> {
        val url = apiCallUrl(path)

        logRequest(url)

        val response = client.submitFormWithBinaryData<HttpResponse>(
            url = url,
            formData = formData
        ) {
            // TODO: update ktor to 1.6.4 for this
            uploadStatusCallback?.let {
                onUpload { bytesSentTotal, contentLength ->
                    it(bytesSentTotal, contentLength)
                }
            }
        }

        return getAndLogResult(response)
    }


    private suspend inline fun <reified T : Any> makeApiCall(path: String,
                                                             body: Message<*, *>): ApiResult<T> {
        val url = apiCallUrl(path)

        logRequest(url, body)

        val response = client.post<HttpResponse> {
            url(url)
            contentType(ContentType.parse(config.mediaType.contentType))
            this.body = body
        }

        return getAndLogResult(response)
    }

    private suspend inline fun <reified T : Any> makeApiCallWithoutBody(path: String,
                                                                        method: HttpMethod = HttpMethod.Get): ApiResult<T> {
        val url = apiCallUrl(path)

        logRequest(url)

        val response = client.request<HttpResponse> {
            url(url)
            contentType(ContentType.parse(config.mediaType.contentType))
            this.method = method
        }

        return getAndLogResult(response)
    }

    override fun getConfig(): ApiClientConfig = config

    override val account: Account = object : Account {
        override suspend fun list(body: AccountsListRequest): ApiResult<AccountsListResponse> =
            makeApiCall("account.list", body)

        override suspend fun updatePassword(body: UpdateAccountPasswordRequest): ApiResult<UpdateAccountPasswordResponse> =
            makeApiCall("account.updatePasswords", body)

        override suspend fun updateName(body: UpdateAccountNameRequest): ApiResult<UpdateAccountNameResponse> =
            makeApiCall("account.updateName", body)

    }
    override val auth: Auth = object : Auth {
        override suspend fun checkEmail(body: CheckEmailRequest): ApiResult<CheckEmailResponse> {
            return makeApiCall("auth.checkEmail", body)
        }

        override suspend fun login(body: LoginRequest): ApiResult<LoginResponse> {
            return makeApiCall("auth.login", body)
        }

        override suspend fun sendMagicLink(body: SendMagicLinkRequest): ApiResult<SendMagicLinkResponse> {
            return makeApiCall("auth.sendMagicLink", body)
        }

        override suspend fun verifyMagicLinkCode(body: VerifyMagicLinkCodeRequest): ApiResult<LoginResponse> {
            return makeApiCall("auth.verifyMagicLinkCode", body)
        }

        override suspend fun verifyMagicLink(body: VerifyMagicLinkRequest): ApiResult<LoginResponse> {
            return makeApiCall("auth.verifyMagicLink", body)
        }

        override suspend fun oauthGoogle(body: LoginWithGoogleRequest): ApiResult<LoginResponse> {
            return makeApiCall("auth.oauth.google", body)
        }

        override suspend fun oauthSlack(body: LoginWithSlackRequest): ApiResult<LoginResponse> {
            return makeApiCall("auth.oauth.slack", body)
        }

    }
    override val avatar: Avatar = object : Avatar {
        override suspend fun uploadFile(file: BinaryContent,
                                        uploadStatusCallback: UploadStatusCallback?): ApiResult<AvatarUploadResponse> {
            return makeMultipartApiCall("avatar.upload", formData {
                appendInput(
                    key = "file",
                    headers = headersOf(
                        HttpHeaders.ContentType to file.contentType,
                        HttpHeaders.ContentDisposition to "filename=${file.name}"
                    ),
                    size = file.size,
                ) {
                    file.open().asInput()
                }
            }, uploadStatusCallback)
        }


        override suspend fun downloadAvatar(avatarId: String,
                                            size: AvatarSize,
                                            targetFile: File): File {
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

        override suspend fun setPrimary(body: SetPrimaryAvatarRequest): ApiResult<SetPrimaryAvatarResponse> {
            return makeApiCall("avatar.setPrimary", body)
        }

        override fun getAvatarUrl(avatarId: String, size: AvatarSize): String {
            return "${config.apiBaseUrl}/avatar.download/${avatarId}/${size.raw}"
        }
    }

    override val chat: Chat = object : Chat {
        override suspend fun get(body: GetChatRequest): ApiResult<GetChatResponse> {
            return makeApiCall("chat.get", body)
        }

        override suspend fun listRecentChats(body: ListRecentChatsRequest): ApiResult<ListRecentChatsResponse> {
            return makeApiCall("chat.recent.list", body)
        }

        override suspend fun create(body: CreateChatRequest): ApiResult<CreateChatResponse> {
            return makeApiCall("chat.create", body)
        }

        override suspend fun update(body: UpdateChatRequest): ApiResult<UpdateChatResponse> {
            return makeApiCall("chat.update", body)
        }

        override suspend fun join(body: JoinChatRequest): ApiResult<JoinChatResponse> {
            return makeApiCall("chat.join", body)
        }

        override suspend fun leave(body: LeaveChatRequest): ApiResult<LeaveChatResponse> {
            return makeApiCall("chat.leave", body)
        }

        override suspend fun archive(body: ArchiveChatRequest): ApiResult<ArchiveChatResponse> {
            return makeApiCall("chat.archive", body)
        }

        override suspend fun markAsRead(body: MarkChatAsReadRequest): ApiResult<MarkChatAsReadResponse> {
            return makeApiCall("chat.markAsRead", body)
        }

        override suspend fun search(body: SearchChatRequest): ApiResult<SearchChatResponse> {
            return makeApiCall("chat.search", body)
        }
    }
    override val chatActivity: ChatActivity = object : ChatActivity {
        override suspend fun start(body: StartChatActivityRequest): ApiResult<StartChatActivityResponse> {
            return makeApiCall("chat.activity.start", body)
        }

        override suspend fun stop(body: StopChatActivityRequest): ApiResult<StopChatActivityResponse> {
            return makeApiCall("chat.activity.stop", body)
        }
    }
    override val chatMessage: ChatMessage = object : ChatMessage {
        override suspend fun get(body: GetChatMessagesRequest): ApiResult<GetChatMessagesResponse> {
            return makeApiCall("chat.message.get", body)
        }

        override suspend fun send(body: SendChatMessageRequest): ApiResult<SendChatMessageResponse> {
            return makeApiCall("chat.message.send", body)
        }

        override suspend fun edit(body: EditChatMessageRequest): ApiResult<EditChatMessageResponse> {
            return makeApiCall("chat.message.edit", body)
        }

        override suspend fun toggleReaction(body: ToggleChatMessageReactionRequest): ApiResult<ToggleChatMessageReactionResponse> {
            return makeApiCall("chat.message.toggleReaction", body)
        }

        override suspend fun remove(body: RemoveChatMessageRequest): ApiResult<RemoveChatMessageResponse> {
            return makeApiCall("chat.message.remove", body)
        }
    }
    override val conference: Conference = object : Conference {
        override suspend fun create(body: CreateConferenceRequest): ApiResult<CreateConferenceResponse> {
            return makeApiCall("conference.create", body)
        }

        override suspend fun join(body: JoinConferenceRequest): ApiResult<JoinConferenceResponse> {
            return makeApiCall("conference.join", body)
        }

        override suspend fun list(body: ConferenceListRequest): ApiResult<ConferenceListResponse> {
            return makeApiCall("conference.list", body)
        }

        override suspend fun leave(body: LeaveConferenceRequest): ApiResult<LeaveConferenceResponse> {
            return makeApiCall("conference.leave", body)
        }

        override suspend fun inviteToConference(body: InviteToConferenceRequest): ApiResult<InviteToConferenceResponse> {
            return makeApiCall("conference.invite", body)
        }

        override suspend fun watercoolerUpdateScope(body: ConferenceWatercoolerUpdateScopeRequest): ApiResult<ConferenceWatercoolerUpdateScopeResponse> {
            return makeApiCall("conference.watercooler.updateScope", body)
        }

        override suspend fun conferenceInfo(body: ConferenceInfoRequest): ApiResult<ConferenceInfoResponse> {
            return makeApiCall("conference.info", body)
        }

        override suspend fun conferenceVoiceChannelUpdateMute(body: ConferenceVoiceChannelUpdateMuteRequest): ApiResult<ConferenceVoiceChannelUpdateMuteResponse> {
            return makeApiCall("conference.voiceChannel.updateMute", body)
        }

    }

    override val file: io.stork.client.module.File = object : io.stork.client.module.File {
        override fun getFileUrl(fileId: String): String {
            return "${config.apiBaseUrl}/file.download/${fileId}"
        }

        override suspend fun getPreSignedUrl(body: GetFilePreSignedUrlRequest): ApiResult<GetFilePreSignedUrlResponse> {
            return makeApiCall("file.getPreSignedUrl", body)
        }

        override suspend fun startMultipart(body: UploadFileRequest): ApiResult<StartMultipartFileUploadResponse> {
            return makeApiCall("file.startMultipart", body)
        }

        override suspend fun finishPart(body: FinishPartUploadRequest): ApiResult<FinishPartUploadResponse> {
            return makeApiCall("file.finishPart", body)
        }

        override suspend fun finishMultipart(body: FinishMultipartFileUploadRequest): ApiResult<FinishMultipartFileUploadResponse> {
            return makeApiCall("file.finishMultipart", body)
        }

        override suspend fun uploadFile(
            body: UploadFileRequest,
            content: File
        ): ApiResult<UploadFileResponse> {
            val url = apiCallUrl("file.directUpload")
            logRequest(url)
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = url,
                formData = formData {
                    append("uploadFileRequest", DefaultProtobufSerializer.write(body).bytes())
                    append("content", content.readBytes(), headersOf(
                        HttpHeaders.ContentDisposition to "filename=${content.name}"
                    ))
                }
            )
            return getAndLogResult<UploadFileResponse>(response)
        }

        override suspend fun getFileMetadata(fileId: String): ApiResult<GetFileMetadataResponse> {
            return makeApiCallWithoutBody("file.metadata/$fileId", HttpMethod.Get)
        }

        override suspend fun deleteFile(fileId: String) {
            makeApiCallWithoutBody<AnyMessage>("file.delete/$fileId", HttpMethod.Delete)
        }
    }

    override val member: Member = object : Member {
        override suspend fun list(body: MemberListRequest): ApiResult<MemberListResponse> {
            return makeApiCall("member.list", body)
        }
    }

    override val publicProfile: PublicProfile = object : PublicProfile {
        override suspend fun list(body: PublicProfileListRequest): ApiResult<PublicProfileListResponse> {
            return makeApiCall("publicProfile.list", body)
        }
    }

    override val recordings: Recordings = object : Recordings {
        override suspend fun list(body: RecordingListRequest): ApiResult<RecordingListResponse> {
            return makeApiCall("recording.list", body)
        }

        override suspend fun updateTitle(body: UpdateRecordingTitleRequest): ApiResult<UpdateRecordingTitleResponse> {
            return makeApiCall("recording.updateRecordingTitle", body)
        }
    }

    override val rtc: RTC = object : RTC {
        override suspend fun negotiateConnection(body: RTCConnectionNegotiateRequest): ApiResult<RTCConnectionNegotiateResponse> {
            return makeApiCall("rtc.negotiate", body)
        }

        override suspend fun addIceCandidates(body: AddRTCIceCandidatesRequest): ApiResult<AddRTCIceCandidatesResponse> {
            return makeApiCall("rtc.addIceCandidates", body)
        }

        override suspend fun removeIceCandidates(body: RemoveRTCIceCandidatesRequest): ApiResult<RemoveRTCIceCandidatesResponse> {
            return makeApiCall("rtc.removeIceCandidates", body)
        }
    }

    override val session: Session = object : Session {
        override suspend fun generate(body: GenerateSessionRequest): ApiResult<GenerateSessionResponse> {
            return makeApiCall("session.generate", body)
        }

        override suspend fun updateClientSystemInfo(body: UpdateClientSystemInfoRequest): ApiResult<UpdateClientSystemInfoResponse> {
            return makeApiCall("session.updateClientSystemInfo", body)
        }

        override suspend fun updateTimezone(body: UpdateTimezoneRequest): ApiResult<UpdateTimezoneResponse> {
            return makeApiCall("session.updateTimezone", body)
        }

        override suspend fun logout(body: LogoutRequest): ApiResult<LogoutResponse> {
            return makeApiCall("session.logout", body)
        }

        override suspend fun addPushNotificationsToken(body: AddPushNotificationsTokenRequest): ApiResult<AddPushNotificationsTokenResponse> {
            return makeApiCall("session.addPushNotificationsToken", body)
        }

        override suspend fun removePushNotificationsToken(body: RemovePushNotificationsTokenRequest): ApiResult<RemovePushNotificationsTokenResponse> {
            return makeApiCall("session.removePushNotificationsToken", body)
        }
    }

    override val workspace: Workspace = object : Workspace {
        override suspend fun join(body: JoinWorkspaceRequest): ApiResult<JoinWorkspaceResponse> {
            return makeApiCall("workspace.join", body)
        }

        override suspend fun invite(body: InviteToWorkspaceRequest): ApiResult<InviteToWorkspaceResponse> {
            return makeApiCall("workspace.invite", body)
        }

        override suspend fun importMembersFromSlack(body: ImportMembersToWorkspaceFromSlackRequest): ApiResult<ImportMembersToWorkspaceFromSlackResponse> {
            return makeApiCall("workspace.importMembers.slack", body)
        }

        override suspend fun importMembersFromGoogle(body: ImportMembersToWorkspaceFromGoogleRequest): ApiResult<ImportMembersToWorkspaceFromGoogleResponse> {
            return makeApiCall("workspace.importMembers.google", body)
        }

        override suspend fun list(body: WorkspaceListRequest): ApiResult<WorkspaceListResponse> {
            return makeApiCall("workspace.list", body)
        }

        override suspend fun create(body: CreateWorkspaceRequest): ApiResult<CreateWorkspaceResponse> {
            return makeApiCall("workspace.create", body)
        }

        override suspend fun checkSubdomain(body: CheckWorkspaceSubdomainRequest): ApiResult<CheckWorkspaceSubdomainResponse> {
            return makeApiCall("workspace.checkSubdomain", body)
        }

        override suspend fun updateDisplayName(body: UpdateWorkspaceDisplayNameRequest): ApiResult<UpdateWorkspaceDisplayNameResponse> {
            return makeApiCall("workspace.updateDisplayName", body)
        }

        override suspend fun leave(body: LeaveWorkspaceRequest): ApiResult<LeaveWorkspaceResponse> {
            return makeApiCall("workspace.leave", body)
        }
    }
}

private fun headersOf(vararg pairs: Pair<String, String>): Headers = headersOf(
    *pairs.map { it.first to listOf(it.second) }.toTypedArray()
)
