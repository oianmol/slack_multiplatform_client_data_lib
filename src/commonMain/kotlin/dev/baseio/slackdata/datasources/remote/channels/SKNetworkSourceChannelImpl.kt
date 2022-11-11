package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.security.RsaEcdsaKeyManagerInstances
import dev.baseio.slackdomain.datasources.IDataEncrypter
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkSourceChannel
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class SKNetworkSourceChannelImpl(
  private val grpcCalls: IGrpcCalls,
  private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
  private val iDataEncrypter: IDataEncrypter
) : SKNetworkSourceChannel {
  override suspend fun inviteUserToChannel(
    userName: String,
    channelId: String,
    workspaceId: String
  ): List<DomainLayerChannels.SkChannelMember> {
    val rsaEcdsaKeyManager = RsaEcdsaKeyManagerInstances.getInstance(channelId)
    val privateKey = rsaEcdsaKeyManager.getPrivateKey()
    privateKey.encoded
    val user = skLocalDataSourceUsers.getUserByUserName(workspaceId, userName)
    user?.let {
      val encrypted = iDataEncrypter.encrypt(privateKey.encoded, user.publicKey!!.keyBytes)
      return grpcCalls.inviteUserToChannel(userName, channelId).membersList.map { kmskChannelMember ->
        DomainLayerChannels.SkChannelMember(
          kmskChannelMember.uuid,
          kmskChannelMember.workspaceId,
          kmskChannelMember.channelId,
          kmskChannelMember.memberId,
          channelEncryptedPrivateKey = encrypted.toSKUserPublicKey()
        )
      }
    } ?: run {
      throw Exception("User Not Found!")
    }

  }
}