package dev.baseio.slackdomain.usecases.users

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAllChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class UseCaseFetchChannelsWithSearch(
  private val useCaseFetchLocalUsers: UseCaseFetchLocalUsers,
  private val useCaseSearchChannel: UseCaseSearchChannel,
  private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels
) {
  operator fun invoke(workspaceId: String, search: String): Flow<List<DomainLayerChannels.SKChannel>> {
    val localUsers = useCaseFetchLocalUsers(workspaceId, search).map { skUsers ->
      skUsers.map { skUser ->
        val dmChannel = skLocalDataSourceReadChannels.getChannelByReceiverId(workspaceId, skUser.uuid)
        DomainLayerChannels.SKChannel.SkDMChannel(
          workId = workspaceId,
          senderId = dmChannel?.senderId ?: "",
          receiverId = skUser.uuid,
          uuid = dmChannel?.uuid ?: "",
          deleted = false
        ).apply {
          channelName = skUser.name
          pictureUrl = skUser.avatarUrl
        }
      }
    }

    val localChannels = useCaseSearchChannel(
      UseCaseWorkspaceChannelRequest(workspaceId = workspaceId, search)
    )

    return combine(localUsers, localChannels) { first, second ->
      return@combine first + second.filterIsInstance<DomainLayerChannels.SKChannel.SkGroupChannel>()
    }
  }
}