package dev.baseio.slackdata.datasources

import dev.baseio.slackdomain.datasources.PublicKeyRetriever
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class PublicKeyRetrieverImpl(
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
    private val skLocalDataSourceChannelMembers: SKLocalDataSourceChannelMembers,
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels,
) : PublicKeyRetriever {

    override fun getMyPublicKey(workspaceId: String,sender: String): ByteArray {
        return skLocalDataSourceUsers.getUser(workspaceId, sender)!!.publicKey!!.keyBytes
    }
    override suspend fun retrieve(sender: String, channelId: String, workspaceId: String): ByteArray {
        val channel = skLocalDataSourceReadChannels.getChannelById(workspaceId, channelId)
        channel?.let { skChannel ->
            when (skChannel) {
                is DomainLayerChannels.SKChannel.SkDMChannel -> {
                    return whenOneToOneChannel(workspaceId, channelId, sender)
                }
                is DomainLayerChannels.SKChannel.SkGroupChannel -> {
                    throw Exception("Not implemented!")
                }
            }
        } ?: kotlin.run {
            throw Exception("Woah! the channel does not exist!")
        }
    }

    private suspend fun whenOneToOneChannel(
        workspaceId: String,
        channelId: String,
        sender: String
    ): ByteArray {
        val members = skLocalDataSourceChannelMembers.getNow(workspaceId, channelId)
        return if (members.size == 1) {
            skLocalDataSourceUsers.getUser(workspaceId, sender)!!.publicKey!!.keyBytes
        } else {
            val otherMember = members.first { it.memberId != sender }
            skLocalDataSourceUsers.getUser(workspaceId, otherMember.memberId)!!.publicKey!!.keyBytes
        }
    }
}