package io.stork.client

import com.google.protobuf.Message
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.stork.client.module.*
import io.stork.client.module.Account
import io.stork.client.module.Auth
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
import io.stork.proto.member.MemberListRequest
import io.stork.proto.member.MemberListResponse
import io.stork.proto.publicProfile.PublicProfileListRequest
import io.stork.proto.publicProfile.PublicProfileListResponse
import io.stork.proto.session.*
import io.stork.proto.workspace.*
import okhttp3.MultipartBody
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

internal class KtorApiClient(
    private val config: ApiClientConfig,
    private val client: HttpClient,
    private val sessionManager: SessionManager,
    override val websocket: EventWebsocket
): ApiClient, SessionManager by sessionManager {
    private val log = LoggerFactory.getLogger("ApiClient")

    private suspend inline fun <reified T> makeApiCall(path: String, body: Message): T {
        val url = config.apiBaseUrl + path

        when (config.logLevel) {
            LogLevel.BASIC -> log.info("$url <<<")
            LogLevel.BODY -> log.info("$url <<< {}", body)
        }

        val response = client.post<HttpResponse> {
            url(url)
            contentType(ContentType.parse(config.mediaType.contentType))
            this.body = body
        }

        val result = response.receive<T>()
        when (config.logLevel) {
            LogLevel.BASIC -> log.info("$url >>> ${response.status}")
            LogLevel.BODY -> log.info("$url >>> {}", result)
        }

        return result
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

        override suspend fun downloadAvatar(avatarId: String, size: Int, targetFile: File): File {
            return client.get<HttpStatement>("avatar.download/$avatarId/$size").execute { response: HttpResponse ->
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

        override suspend fun createConnection(body: CreateConferenceRTCConnectionRequest): CreateConferenceRTCConnectionResponse {
            return makeApiCall("conference.createConnection", body)
        }

        override suspend fun closeConnection(body: CloseConferenceRTCConnectionRequest): CloseConferenceRTCConnectionResponse {
            return makeApiCall("conference.closeConnection", body)
        }

        override suspend fun leave(body: LeaveConferenceRequest): LeaveConferenceResponse {
            return makeApiCall("conference.leave", body)
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

    override val rtc: RTC = object: RTC {
        override suspend fun createOffer(body: CreateRTCConnectionOfferRequest): CreateRTCConnectionOfferResponse {
            return makeApiCall("rtc.createOffer", body)
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