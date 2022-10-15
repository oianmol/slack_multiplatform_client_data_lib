package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.*

class UseCaseFetchAndSaveChannels(
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels,
    private val skNetworkDataSourceReadChannels: SKNetworkDataSourceReadChannels,
    private val skLocalDataSourceWriteChannels: SKLocalDataSourceCreateChannels
) {
    suspend operator fun invoke(
        workspaceId: String,
        offset: Int,
        limit: Int
    ): Flow<List<DomainLayerChannels.SKChannel>> {
        return skNetworkDataSourceReadChannels.fetchChannels(workspaceId = workspaceId, offset, limit)
            .let { skChannelList ->
                skChannelList.map { skChannel ->
                    skLocalDataSourceWriteChannels.saveChannel(skChannel)
                }
                skChannelList
            }.let {
                skLocalDataSourceReadChannels.fetchChannels(workspaceId)
            }.catch {
                skLocalDataSourceReadChannels.fetchChannels(workspaceId)
            }
    }

}
