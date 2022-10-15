package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.Flow

class UseCaseFetchAllChannels(private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels) {
    operator fun invoke(workspaceId:String): Flow<List<DomainLayerChannels.SKChannel>> {
        return skLocalDataSourceReadChannels.fetchChannels(workspaceId)
    }
}