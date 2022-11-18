package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.OAEPParameterSpec
import dev.baseio.security.Padding
import dev.baseio.security.CapillaryInstances
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.datasources.IDataDecryptor
import dev.baseio.slackdomain.datasources.IDataEncrypter
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkSourceChannel
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
class SKNetworkSourceChannelImpl(
    private val grpcCalls: IGrpcCalls,
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
    private val skLocalDataSourceChannelMembers: SKLocalDataSourceChannelMembers,
    private val iDataEncrypter: IDataEncrypter,
    private val skLocalKeyValueSource: SKLocalKeyValueSource,
    private val iDataDecryptor: IDataDecryptor
) : SKNetworkSourceChannel {

    private suspend fun inviteUserInternal(
        userName: String,
        channelId: String,
        channelEncryptedPrivateKey: ByteArray
    ): List<DomainLayerChannels.SkChannelMember> {
        return grpcCalls.inviteUserToChannel(
            userName,
            channelId,
            channelEncryptedPrivateKey.toSKUserPublicKey()
        ).membersList.map { kmskChannelMember ->
            DomainLayerChannels.SkChannelMember(
                kmskChannelMember.uuid,
                kmskChannelMember.workspaceId,
                kmskChannelMember.channelId,
                kmskChannelMember.memberId,
                channelEncryptedPrivateKey = kmskChannelMember.channelPrivateKey.toUserPublicKey()
            )
        }.also {
            skLocalDataSourceChannelMembers.save(it)
        }
    }

    override suspend fun inviteUserToChannelFromOtherDeviceOrUser(
        channel: DomainLayerChannels.SKChannel,
        userName: String
    ): List<DomainLayerChannels.SkChannelMember> {
        val channelEncryptedPrivateKeyForLoggedInUser = skLocalDataSourceChannelMembers.getChannelPrivateKeyForMe(
            channel.workspaceId,
            channel.channelId,
            skLocalKeyValueSource.skUser().uuid
        )!!.channelEncryptedPrivateKey.keyBytes
        val capillary =
            CapillaryInstances.getInstance(skLocalKeyValueSource.skUser().email!!)
        val decryptedChannelPrivateKeyForLoggedInUser = capillary.decrypt(
            channelEncryptedPrivateKeyForLoggedInUser, capillary.privateKey()
        )
        val channelPrivateKeyEncryptedForInvitedUser = iDataEncrypter.encrypt(
            decryptedChannelPrivateKeyForLoggedInUser,
            skLocalDataSourceUsers.getUserByUserName(channel.workspaceId, userName)!!.publicKey!!.keyBytes
        ) // TODO fix this ask the backend if not available in local cache!
        return inviteUserInternal(userName, channel.channelId, channelPrivateKeyEncryptedForInvitedUser)
    }
}