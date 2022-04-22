package io.stork.client.module

import io.stork.client.ApiResult
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

interface Workspace {
    suspend fun join(body: JoinWorkspaceRequest): ApiResult<JoinWorkspaceResponse>

    suspend fun invite(body: InviteToWorkspaceRequest): ApiResult<InviteToWorkspaceResponse>

    suspend fun importMembersFromSlack(body: ImportMembersToWorkspaceFromSlackRequest): ApiResult<ImportMembersToWorkspaceFromSlackResponse>

    suspend fun importMembersFromGoogle(body: ImportMembersToWorkspaceFromGoogleRequest): ApiResult<ImportMembersToWorkspaceFromGoogleResponse>

    suspend fun list(body: WorkspaceListRequest): ApiResult<WorkspaceListResponse>

    suspend fun create(body: CreateWorkspaceRequest): ApiResult<CreateWorkspaceResponse>

    suspend fun checkSubdomain(body: CheckWorkspaceSubdomainRequest): ApiResult<CheckWorkspaceSubdomainResponse>

    suspend fun updateDisplayName(body: UpdateWorkspaceDisplayNameRequest): ApiResult<UpdateWorkspaceDisplayNameResponse>

    suspend fun leave(body: LeaveWorkspaceRequest): ApiResult<LeaveWorkspaceResponse>
}