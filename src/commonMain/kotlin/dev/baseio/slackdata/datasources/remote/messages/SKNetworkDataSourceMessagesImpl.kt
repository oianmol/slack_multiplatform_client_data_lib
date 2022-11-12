package dev.baseio.slackdata.datasources.remote.messages

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.protos.KMSKMessage
import dev.baseio.slackdata.protos.kmSKMessage
import dev.baseio.slackdomain.datasources.IDataEncrypter
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SKNetworkDataSourceMessagesImpl(
    private val grpcCalls: IGrpcCalls, private val iDataEncrypter: IDataEncrypter
) : SKNetworkDataSourceMessages {

    override fun registerChangeInMessages(request: UseCaseWorkspaceChannelRequest): Flow<Pair<DomainLayerMessages.SKMessage?, DomainLayerMessages.SKMessage?>> {
        return grpcCalls.listenToChangeInMessages(request).map { message ->
            Pair(
                if (message.hasPrevious()) message.previous.toDomainLayerMessage() else null,
                if (message.hasLatest()) message.latest.toDomainLayerMessage() else null
            )
        }
    }

    override suspend fun fetchMessages(request: UseCaseWorkspaceChannelRequest): Result<List<DomainLayerMessages.SKMessage>> {
        return kotlin.runCatching {
            grpcCalls.fetchMessages(request).messagesList.map {
                it.toDomainLayerMessage()
            }
        }
    }

    override suspend fun deleteMessage(
        params: DomainLayerMessages.SKMessage,
        publicKey: DomainLayerUsers.SKUserPublicKey
    ): DomainLayerMessages.SKMessage {
        return grpcCalls.sendMessage(kmSKMessage {
            uuid = params.uuid
            workspaceId = params.workspaceId
            isDeleted = params.isDeleted
            channelId = params.channelId
            textList.addAll( // don't encrypte the message again!
                params.message.map {
                    kmSKByteArrayElement {
                        this.byte = it.toInt()
                    }
                }
            )
            sender = params.sender
            createdDate = params.createdDate
            modifiedDate = params.modifiedDate
        }).toDomainLayerMessage()
    }

    override suspend fun sendMessage(params: DomainLayerMessages.SKMessage, publicKey: DomainLayerUsers.SKUserPublicKey): DomainLayerMessages.SKMessage {
        val encryptedMessage = iDataEncrypter.encrypt(
            params.message,
            publicKey.keyBytes,
        )
        return grpcCalls.sendMessage(kmSKMessage {
            uuid = params.uuid
            workspaceId = params.workspaceId
            isDeleted = params.isDeleted
            channelId = params.channelId
            textList.addAll(
                encryptedMessage.map {
                    kmSKByteArrayElement {
                        this.byte = it.toInt()
                    }
                }
            )
            sender = params.sender
            createdDate = params.createdDate
            modifiedDate = params.modifiedDate
        }).toDomainLayerMessage()
    }
}

fun KMSKMessage.toDomainLayerMessage(): DomainLayerMessages.SKMessage {
    val params = this
    return DomainLayerMessages.SKMessage(
        uuid = params.uuid,
        workspaceId = params.workspaceId,
        channelId = params.channelId,
        message = params.textList.map { it.byte.toByte() }.toByteArray(),
        sender = params.sender,
        createdDate = params.createdDate,
        modifiedDate = params.modifiedDate,
        isDeleted = params.isDeleted,
        isSynced = true,
    )
}
