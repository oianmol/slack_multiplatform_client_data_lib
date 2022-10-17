package dev.baseio.slackdomain.usecases.users

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAllChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class UseCaseFetchChannelsWithSearch(private val useCaseFetchLocalUsers: UseCaseFetchLocalUsers,
                                     private val useCaseSearchChannel: UseCaseSearchChannel
) {
  operator fun invoke(workspaceId: String, search: String): Flow<List<DomainLayerChannels.SKChannel>> {
    val localUsers = useCaseFetchLocalUsers(workspaceId, search).map {
      it.map { skUser ->
        DomainLayerChannels.SKChannel.SkDMChannel(
          workId = workspaceId,
          senderId = "",
          receiverId = skUser.uuid,
          uuid = "",
          deleted = false
        ).apply {
          channelName = skUser.name
          pictureUrl = skUser.avatarUrl
        }
      }
    }

   val localChannels=  useCaseSearchChannel(
      UseCaseWorkspaceChannelRequest(workspaceId = workspaceId, search)
    )

   return combine(localUsers,localChannels) { first, second ->
      return@combine first.map { locaUserDmChannel ->
        second.forEach { skChannel ->
          if (skChannel is DomainLayerChannels.SKChannel.SkDMChannel
            && skChannel.receiverId == locaUserDmChannel.receiverId
          ) {
            locaUserDmChannel.receiverId = skChannel.receiverId
            locaUserDmChannel.senderId = skChannel.senderId
            locaUserDmChannel.uuid = skChannel.uuid
            locaUserDmChannel.channelId = skChannel.uuid
          }
        }
        locaUserDmChannel
      } + second
    }
  }
}