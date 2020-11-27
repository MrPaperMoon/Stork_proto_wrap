package io.stork.client.retrofit.module

import io.stork.proto.publicProfile.PublicProfileListRequest
import io.stork.proto.publicProfile.PublicProfileListResponse
import io.stork.proto.workspace.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Workspace {
    @POST("workspace.join")
    fun join(@Body body:JoinWorkspaceRequest): Call<JoinWorkspaceResponse>

    @POST("workspace.invite")
    fun invite(@Body body:InviteToWorkspaceRequest): Call<InviteToWorkspaceResponse>

    @POST("workspace.importMembers.slack")
    fun importMembersFromSlack(@Body body:ImportMembersToWorkspaceFromSlackRequest): Call<ImportMembersToWorkspaceFromSlackResponse>

    @POST("workspace.importMembers.google")
    fun importMembersFromGoogle(@Body body:ImportMembersToWorkspaceFromGoogleRequest): Call<ImportMembersToWorkspaceFromGoogleResponse>

    @POST("workspace.list")
    fun list(@Body body:WorkspaceListRequest): Call<WorkspaceListResponse>

    @POST("workspace.create")
    fun create(@Body body:CreateWorkspaceRequest): Call<CreateWorkspaceResponse>

    @POST("workspace.checkSubdomain")
    fun checkSubdomain(@Body body:CheckWorkspaceSubdomainRequest): Call<CheckWorkspaceSubdomainResponse>
}