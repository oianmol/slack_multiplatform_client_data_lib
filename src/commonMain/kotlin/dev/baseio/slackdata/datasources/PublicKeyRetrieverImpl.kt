package dev.baseio.slackdata.datasources

import dev.baseio.slackdomain.datasources.PublicKeyRetriever
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers

class PublicKeyRetrieverImpl(
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
    private val skLocalDataSourceChannelMembers: SKLocalDataSourceChannelMembers
) : PublicKeyRetriever {
    override suspend fun retrieve(sender: String, channelId: String, workspaceId: String): ByteArray {
        val members = skLocalDataSourceChannelMembers.getNow(workspaceId, channelId)
        if (members.size == 1) {
            return skLocalDataSourceUsers.getUser(workspaceId, sender)!!.publicKey!!.keyBytes
        }
        val otherMember = members.first { it.memberId != sender }
        return skLocalDataSourceUsers.getUser(workspaceId, otherMember.memberId)!!.publicKey!!.keyBytes
    }
}