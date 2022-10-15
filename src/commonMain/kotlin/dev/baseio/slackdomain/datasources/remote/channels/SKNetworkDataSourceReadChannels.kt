package dev.baseio.slackdomain.datasources.remote.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.Flow

interface SKNetworkDataSourceReadChannels {
  suspend fun fetchChannels(workspaceId: String): List<DomainLayerChannels.SKChannel>
}