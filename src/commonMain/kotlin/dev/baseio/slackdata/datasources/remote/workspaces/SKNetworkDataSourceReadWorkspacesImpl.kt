package dev.baseio.slackdata.datasources.remote.workspaces

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.Email
import dev.baseio.slackdomain.usecases.workspaces.Name
import kotlinx.coroutines.flow.Flow

class SKNetworkDataSourceReadWorkspacesImpl(private val grpcCalls: IGrpcCalls) : SKNetworkDataSourceReadWorkspaces {
  override suspend fun findWorkspacesForEmail(email: Email): List<DomainLayerWorkspaces.SKWorkspace> {
    return kotlin.runCatching {
      val workspaces = grpcCalls.findWorkspacesForEmail(email)
      workspaces.workspacesList.map { kmskWorkspace ->
        kmskWorkspace.skWorkspace()
      }
    }.getOrThrow()
  }

  override suspend fun findWorkspaceByName(name: Name): DomainLayerWorkspaces.SKWorkspace {
    return kotlin.runCatching {
      val workspace = grpcCalls.findWorkspaceByName(name)
      workspace.skWorkspace()
    }.getOrThrow()
  }

  override suspend fun getWorkspaces(): List<DomainLayerWorkspaces.SKWorkspace> {
    return kotlin.runCatching {
      val workspaces = grpcCalls.getWorkspaces()
      workspaces.workspacesList.map { kmskWorkspace ->
        kmskWorkspace.skWorkspace()
      }
    }.getOrThrow()
  }
}

fun KMSKWorkspace.skWorkspace() =
  DomainLayerWorkspaces.SKWorkspace(
    this.uuid,
    this.name,
    this.domain,
    this.picUrl,
    this.modifiedTime
  )