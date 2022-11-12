package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.security.RsaEcdsaKeyManagerInstances
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

    override suspend fun addUsersToChannelOnceCreated(channelNew: DomainLayerChannels.SKChannel): List<DomainLayerChannels.SkChannelMember> {
        return when (channelNew) {
            is DomainLayerChannels.SKChannel.SkDMChannel -> {
                if (channelNew.senderId == channelNew.receiverId) {
                    inviteUserToChannelOnceCreated(channelNew.senderId, channelNew.channelId, channelNew.workspaceId)
                } else {
                    inviteUserToChannelOnceCreated(channelNew.senderId, channelNew.channelId, channelNew.workspaceId)
                    inviteUserToChannelOnceCreated(channelNew.receiverId, channelNew.channelId, channelNew.workspaceId)
                }
            }

            is DomainLayerChannels.SKChannel.SkGroupChannel -> {
                inviteUserToChannelOnceCreated(
                    skLocalKeyValueSource.skUser().uuid,
                    channelNew.channelId,
                    channelNew.workspaceId
                )
            }
        }

    }

    private suspend fun inviteUserToChannelOnceCreated(
        userName: String,
        channelId: String,
        workspaceId: String
    ): List<DomainLayerChannels.SkChannelMember> {
        val rsaEcdsaKeyManager = RsaEcdsaKeyManagerInstances.getInstance(channelId)
        val channelPrivateKey = rsaEcdsaKeyManager.getPrivateKey()

        val user = kotlin.runCatching {
            skLocalDataSourceUsers.getUserByUserName(workspaceId, userName)
        }.exceptionOrNull()?.let {
            skLocalDataSourceUsers.getUser(workspaceId, userName)
        }
        user?.let {
            // here we encrypt the channel's private key with the invited users public key
            val encrypted = iDataEncrypter.encrypt(
                channelPrivateKey.encoded,
                user.publicKey!!.keyBytes
            )
            return inviteUserInternal(userName, channelId, encrypted)
        } ?: run {
            throw Exception("User Not Found!")
        }

    }

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
        val myPrivateKeyForDecrypting =
            RsaEcdsaKeyManagerInstances.getInstance(skLocalKeyValueSource.skUser().email!!).getPrivateKey().encoded
        val decryptedChannelPrivateKeyForLoggedInUser =
            iDataDecryptor.decrypt(channelEncryptedPrivateKeyForLoggedInUser, myPrivateKeyForDecrypting)
        val channelPrivateKeyEncryptedForInvitedUser = iDataEncrypter.encrypt(
            decryptedChannelPrivateKeyForLoggedInUser,
            skLocalDataSourceUsers.getUserByUserName(channel.workspaceId, userName)!!.publicKey!!.keyBytes
        ) // TODO fix this ask the backend if not available in local cache!
        return inviteUserInternal(userName, channel.channelId, channelPrivateKeyEncryptedForInvitedUser)
    }
}