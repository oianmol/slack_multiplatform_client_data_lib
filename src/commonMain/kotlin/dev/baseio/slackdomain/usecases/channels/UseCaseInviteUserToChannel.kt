package dev.baseio.slackdomain.usecases.channels

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.datasources.remote.channels.toDomain
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class UseCaseInviteUserToChannel(private val grpcCalls: GrpcCalls) {
  suspend operator fun invoke(userId: String, channelId: String): Result<List<DomainLayerChannels.SkChannelMember>> {
    return kotlin.runCatching {
      grpcCalls.inviteUserToChannel(userId, channelId).membersList.map {
        it.toDomain()
      }
    }
  }
}