package dev.baseio.slackdata.datasources.local.channels

import database.SlackChannelMember
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.ProtoExtensions.asByteArray
import dev.baseio.slackdata.ProtoExtensions.asSKEncryptedMessageFromBytes
import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.datasources.remote.channels.toDomainSKEncryptedMessage
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.protos.KMSKEncryptedMessage
import dev.baseio.slackdata.protos.kmSKEncryptedMessage
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SKLocalDataSourceChannelMembersImpl(
    private val slackDB: SlackDB,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKLocalDataSourceChannelMembers {

    override fun get(
        workspaceId: String,
        channelId: String
    ): Flow<List<DomainLayerChannels.SkChannelMember>> {
        return slackDB.slackDBQueries.selectAllMembers(channelId, workspaceId).asFlow().mapToList()
            .map {
                it.map { it.toChannelMember() }
            }
    }

    override suspend fun getNow(
        workspaceId: String,
        channelId: String
    ): List<DomainLayerChannels.SkChannelMember> {
        return slackDB.slackDBQueries.selectAllMembers(channelId, workspaceId).executeAsList().map {
            it.toChannelMember()
        }
    }

    override fun getChannelPrivateKeyForMe(
        workspaceId: String,
        channelId: String,
        uuid: String
    ): DomainLayerChannels.SkChannelMember? {
        return slackDB.slackDBQueries.getChannelPrivateKeyForUser(channelId, workspaceId, uuid)
            .executeAsOneOrNull()
            ?.toChannelMember()
    }

    override suspend fun save(members: List<DomainLayerChannels.SkChannelMember>) {
        withContext(coroutineDispatcherProvider.io) {
            members.forEach { skChannelMember ->
                slackDB.slackDBQueries.insertMember(
                    skChannelMember.uuid,
                    skChannelMember.workspaceId,
                    skChannelMember.channelId,
                    skChannelMember.memberId,
                    skChannelMember.channelEncryptedPrivateKey.asKMSKEncryptedMessage()
                )
            }
        }

    }
}

fun DomainLayerUsers.SKEncryptedMessage.asKMSKEncryptedMessage(): ByteArray {
    return kmSKEncryptedMessage {
        this.firstList.addAll(this@asKMSKEncryptedMessage.first.map {
            kmSKByteArrayElement {
                this.byte = it.toInt()
            }
        })
        this.secondList.addAll(this@asKMSKEncryptedMessage.second.map {
            kmSKByteArrayElement {
                this.byte = it.toInt()
            }
        })
    }.asByteArray()
}

fun DomainLayerUsers.SKEncryptedMessage.asKMSKEncryptedMessageRaw(): KMSKEncryptedMessage {
    return kmSKEncryptedMessage {
        this.firstList.addAll(this@asKMSKEncryptedMessageRaw.first.map {
            kmSKByteArrayElement {
                this.byte = it.toInt()
            }
        })
        this.secondList.addAll(this@asKMSKEncryptedMessageRaw.second.map {
            kmSKByteArrayElement {
                this.byte = it.toInt()
            }
        })
    }
}

fun SlackChannelMember.toChannelMember(): DomainLayerChannels.SkChannelMember {
    return DomainLayerChannels.SkChannelMember(
        this.uuid,
        this.workspaceId,
        this.channelId,
        this.memberId,
        this.channelPrivateKey.asSKEncryptedMessageFromBytes().toDomainSKEncryptedMessage()
    )
}
