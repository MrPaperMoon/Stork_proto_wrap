package io.stork.client.module

import io.stork.proto.publicProfile.PublicProfileListRequest
import io.stork.proto.publicProfile.PublicProfileListResponse
import io.stork.proto.workspace.*
import retrofit2.http.Body
import retrofit2.http.POST

interface Workspace {
    @POST("publicProfile.list")
    suspend fun list(@Body body:PublicProfileListRequest): PublicProfileListResponse

    @POST("workspace.join")
    suspend fun join(@Body body:JoinWorkspaceRequest): JoinWorkspaceResponse

    @POST("workspace.invite")
    suspend fun invite(@Body body:InviteToWorkspaceRequest): InviteToWorkspaceResponse

    @POST("workspace.importMembers.slack")
    suspend fun importMembersFromSlack(@Body body:ImportMembersToWorkspaceFromSlackRequest): ImportMembersToWorkspaceFromSlackResponse

    @POST("workspace.importMembers.google")
    suspend fun importMembersFromGoogle(@Body body:ImportMembersToWorkspaceFromGoogleRequest): ImportMembersToWorkspaceFromGoogleResponse

    @POST("workspace.list")
    suspend fun list(@Body body:WorkspaceListRequest): WorkspaceListResponse

    @POST("workspace.create")
    suspend fun create(@Body body:CreateWorkspaceRequest): CreateWorkspaceResponse

    @POST("workspace.checkSubdomain")
    suspend fun checkSubdomain(@Body body:CheckWorkspaceSubdomainRequest): CheckWorkspaceSubdomainResponse

}