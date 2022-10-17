package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class SKNetworkDataSourceReadChannelsImpl(
  private val grpcCalls: GrpcCalls,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKNetworkDataSourceReadChannels {

  override fun listenToChangeInChannels(workspaceId: String): Flow<Pair<DomainLayerChannels.SKChannel?, DomainLayerChannels.SKChannel?>> {
    return grpcCalls.listenToChangeInChannels(workspaceId).map { channel ->
      Pair(
        if (channel.hasPrevious()) channel.previous.mapToDomainSkChannel() else null,
        if (channel.hasLatest()) channel.latest.mapToDomainSkChannel() else null
      )
    }.catch {
      // notify upstream for these errors
    }
  }

  override suspend fun fetchChannels(
    workspaceId: String,
    offset: Int,
    limit: Int
  ): Result<List<DomainLayerChannels.SKChannel>> {
    return withContext(coroutineDispatcherProvider.io) {
      kotlin.runCatching {
        grpcCalls.getPublicChannels(workspaceId, offset, limit).run {
          this.channelsList.map {
            it.mapToDomainSkChannel()
          }
        }
      }
    }

  }
}