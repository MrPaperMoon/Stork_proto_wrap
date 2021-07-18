package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.workspace.*

interface Workspace {
    suspend fun join(body: JoinWorkspaceRequest): ApiResult<JoinWorkspaceResponse>

    suspend fun invite(body: InviteToWorkspaceRequest): ApiResult<InviteToWorkspaceResponse>

    suspend fun importMembersFromSlack(body: ImportMembersToWorkspaceFromSlackRequest): ApiResult<ImportMembersToWorkspaceFromSlackResponse>

    suspend fun importMembersFromGoogle(body: ImportMembersToWorkspaceFromGoogleRequest): ApiResult<ImportMembersToWorkspaceFromGoogleResponse>

    suspend fun list(body: WorkspaceListRequest): ApiResult<WorkspaceListResponse>

    suspend fun create(body: CreateWorkspaceRequest): ApiResult<CreateWorkspaceResponse>

    suspend fun checkSubdomain(body: CheckWorkspaceSubdomainRequest): ApiResult<CheckWorkspaceSubdomainResponse>
}