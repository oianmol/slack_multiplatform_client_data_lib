package dev.baseio.slackdata.datasources.remote.workspaces

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKCreateWorkspaceRequest
import dev.baseio.slackdata.protos.kmSKUser
import dev.baseio.slackdata.protos.kmSKWorkspace
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkSourceWorkspaces
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class SKNetworkSourceWorkspacesImpl(private val grpcCalls: GrpcCalls) : SKNetworkSourceWorkspaces {
  override suspend fun saveWorkspace(email: String, password: String, domain: String): DomainLayerUsers.SKAuthResult {
    return kotlin.runCatching {
      val result = grpcCalls.saveWorkspace(kmskCreateWorkspaceRequest(email, password, domain))
      DomainLayerUsers.SKAuthResult(
        result.token,
        result.refreshToken,
        DomainLayerUsers.SKStatus(result.status.information, result.status.statusCode)
      )
    }.getOrThrow()
  }
}

private fun kmskCreateWorkspaceRequest(
  email: String,
  password: String,
  domain: String
) = kmSKCreateWorkspaceRequest {
  this.user = kmSKAuthUser {
    this.email = email
    this.password = password
    this.user = kmSKUser {
      this.email = email
    }
  }
  this.workspace = kmSKWorkspace {
    this.name = domain
  }
}