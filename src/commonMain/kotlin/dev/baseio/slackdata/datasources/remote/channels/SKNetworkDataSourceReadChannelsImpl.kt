package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.*

class SKNetworkDataSourceReadChannelsImpl(private val grpcCalls: GrpcCalls) : SKNetworkDataSourceReadChannels {
    override suspend fun fetchChannels(
        workspaceId: String,
        offset: Int,
        limit: Int
    ): List<DomainLayerChannels.SKChannel> {
        return grpcCalls.getChannels(workspaceId, offset, limit).run {
            this.channelsList.map {
                it.mapToDomainSkChannel()
            }
        }
    }
}