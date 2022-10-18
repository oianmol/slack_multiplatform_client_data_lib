package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UseCaseGetChannelMembers(
  private val localSource: SKLocalDataSourceChannelMembers,
  private val skLocalDataSourceUsers: SKLocalDataSourceUsers
) {
  operator fun invoke(useCaseWorkspaceChannelRequest: UseCaseWorkspaceChannelRequest): Flow<List<DomainLayerUsers.SKUser>> {
    return localSource.get(useCaseWorkspaceChannelRequest.workspaceId, useCaseWorkspaceChannelRequest.channelId!!).map {
      it.mapNotNull {
        skLocalDataSourceUsers.getUser(useCaseWorkspaceChannelRequest.workspaceId, it.memberId)
      }
    }
  }
}