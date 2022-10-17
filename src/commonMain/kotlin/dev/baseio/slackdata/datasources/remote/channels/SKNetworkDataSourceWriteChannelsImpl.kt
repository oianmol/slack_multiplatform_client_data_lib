package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKChannel
import dev.baseio.slackdata.protos.KMSKDMChannel
import dev.baseio.slackdata.protos.kmSKChannel
import dev.baseio.slackdata.protos.kmSKDMChannel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceWriteChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.withContext

class SKNetworkDataSourceWriteChannelsImpl(
  private val grpcCalls: GrpcCalls,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKNetworkDataSourceWriteChannels {
  override suspend fun createChannel(params: DomainLayerChannels.SKChannel): Result<DomainLayerChannels.SKChannel> {
    return withContext(coroutineDispatcherProvider.io) {
      kotlin.runCatching {
        when (params) {
          is DomainLayerChannels.SKChannel.SkDMChannel -> {
            grpcCalls.saveDMChannel(kmSKDMChannel {
              params.uuid.takeIf { it.isNotEmpty() }?.let {
                uuid = params.uuid
              }
              workspaceId = params.workId
              createdDate = params.createdDate
              modifiedDate = params.modifiedDate
              this.senderId = params.senderId
              this.receiverId = params.receiverId
              this.isDeleted = params.deleted
            }).mapToDomainSkChannel()
          }
          is DomainLayerChannels.SKChannel.SkGroupChannel -> {
            grpcCalls.savePublicChannel(kmSKChannel {
              uuid = params.uuid
              workspaceId = params.workId
              name = params.name
              createdDate = params.createdDate
              modifiedDate = params.modifiedDate
              avatarUrl = params.avatarUrl
              this.isDeleted = params.deleted
            }).mapToDomainSkChannel()
          }
        }
      }
    }


  }
}

fun KMSKChannel.mapToDomainSkChannel(): DomainLayerChannels.SKChannel {
  val params = this
  return DomainLayerChannels.SKChannel.SkGroupChannel(
    uuid = params.uuid,
    workId = params.workspaceId,
    name = params.name,
    createdDate = params.createdDate,
    modifiedDate = params.modifiedDate,
    avatarUrl = params.avatarUrl,
    deleted = params.isDeleted
  )
}

fun KMSKDMChannel.mapToDomainSkChannel(): DomainLayerChannels.SKChannel {
  val params = this
  return DomainLayerChannels.SKChannel.SkDMChannel(
    uuid = params.uuid,
    workId = params.workspaceId,
    createdDate = params.createdDate,
    modifiedDate = params.modifiedDate,
    deleted = params.isDeleted,
    senderId = params.senderId,
    receiverId = params.receiverId
  )
}
