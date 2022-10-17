package dev.baseio.slackdata.datasources.remote.users

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.datasources.remote.messages.toDomainLayerMessage
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.auth.toSKUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class SKNetworkDataSourceReadUsersImpl(
  private val grpcCalls: GrpcCalls,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKNetworkDataSourceReadUsers {
  override suspend fun fetchUsers(workspaceId: String): Result<List<DomainLayerUsers.SKUser>> {
    return withContext(coroutineDispatcherProvider.io) {
      kotlin.runCatching {
        val users = grpcCalls.getUsersForWorkspaceId(workspaceId)
        users.usersList.map { it.toSKUser() }
      }
    }
  }

  override fun listenToChangeInUsers(workspaceId: String): Flow<Pair<DomainLayerUsers.SKUser?, DomainLayerUsers.SKUser?>> {
    return grpcCalls.listenToChangeInUsers(workspaceId = workspaceId).map { message ->
      Pair(
        if (message.hasPrevious()) message.previous.toSKUser() else null,
        if (message.hasLatest()) message.latest.toSKUser() else null
      )
    }.catch {
      // notify upstream for these errors
    }
  }
}