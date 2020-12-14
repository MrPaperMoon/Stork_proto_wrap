package io.stork.client.module

import io.stork.proto.workspace.*

interface Workspace {
    suspend fun join(body: JoinWorkspaceRequest): JoinWorkspaceResponse

    suspend fun invite(body: InviteToWorkspaceRequest): InviteToWorkspaceResponse

    suspend fun importMembersFromSlack(body: ImportMembersToWorkspaceFromSlackRequest): ImportMembersToWorkspaceFromSlackResponse

    suspend fun importMembersFromGoogle(body: ImportMembersToWorkspaceFromGoogleRequest): ImportMembersToWorkspaceFromGoogleResponse

    suspend fun list(body: WorkspaceListRequest): WorkspaceListResponse

    suspend fun create(body: CreateWorkspaceRequest): CreateWorkspaceResponse

    suspend fun checkSubdomain(body: CheckWorkspaceSubdomainRequest): CheckWorkspaceSubdomainResponse
}