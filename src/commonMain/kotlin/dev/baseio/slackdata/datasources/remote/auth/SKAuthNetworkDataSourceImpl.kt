package dev.baseio.slackdata.datasources.remote.auth

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKUser
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class SKAuthNetworkDataSourceImpl(private val grpcCalls: IGrpcCalls) : SKAuthNetworkDataSource {
  override suspend fun login(
    email: String,
    password: String,
    workspaceId: String
  ): Result<DomainLayerUsers.SKAuthResult> {
    return kotlin.runCatching {
      val result = grpcCalls.login(kmSKAuthUser {
        this.email = email
        this.password = password
        this.user = kmSKUser {
          this.workspaceId = workspaceId
        }
      })
      DomainLayerUsers.SKAuthResult(
        result.token,
        result.refreshToken,
        DomainLayerUsers.SKStatus(result.status.information, result.status.statusCode)
      )
    }
  }

  override suspend fun register(skAuthUser: DomainLayerUsers.SkAuthUser): Result<DomainLayerUsers.SKAuthResult> {
    return kotlin.runCatching {
      val result = grpcCalls.register(kmSKAuthUser {
        this.email = email
        this.password = password
        this.user = kmSKUser {
          this.workspaceId = workspaceId
        }
      })
      DomainLayerUsers.SKAuthResult(
        result.token,
        result.refreshToken,
        DomainLayerUsers.SKStatus(result.status.information, result.status.statusCode)
      )
    }
  }

  override suspend fun getLoggedInUser(): Result<DomainLayerUsers.SKUser> {
    return kotlin.runCatching {
      val result = grpcCalls.currentLoggedInUser()
      DomainLayerUsers.SKUser(
        result.uuid,
        result.workspaceId,
        result.gender,
        result.name,
        result.location,
        result.email,
        result.username,
        result.userSince,
        result.phone,
        result.avatarUrl
      )
    }
  }
}