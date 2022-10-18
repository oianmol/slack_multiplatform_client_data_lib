package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UseCaseFetchAllChannels(
  private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels,
) {
  operator fun invoke(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>> {
    return skLocalDataSourceReadChannels.fetchAllChannels(workspaceId)
  }


}

fun DomainLayerUsers.SKUser.otherUserInDMChannel(
  skChannel: DomainLayerChannels.SKChannel.SkDMChannel
) = if (this.uuid == skChannel.receiverId) {
  skChannel.senderId
} else {
  skChannel.receiverId
}