package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkSourceChannel
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class SKNetworkSourceChannelImpl(private val grpcCalls: IGrpcCalls) : SKNetworkSourceChannel {
  override suspend fun inviteUserToChannel(
    userId: String,
    channelId: String
  ): List<DomainLayerChannels.SkChannelMember> {
    return grpcCalls.inviteUserToChannel(userId, channelId).membersList.map { kmskChannelMember ->
      DomainLayerChannels.SkChannelMember(kmskChannelMember.uuid, kmskChannelMember.workspaceId, kmskChannelMember.channelId, kmskChannelMember.memberId)
    }
  }
}