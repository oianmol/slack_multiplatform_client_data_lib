package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class UseCaseFetchMessages(private val skLocalDataSourceMessages: SKLocalDataSourceMessages,private val skLocalDataSourceUsers: SKLocalDataSourceUsers) {
    operator fun invoke(useCaseChannelRequest: UseCaseChannelRequest): Flow<List<DomainLayerMessages.SKMessage>> {
        return skLocalDataSourceMessages.streamLocalMessages(
            workspaceId = useCaseChannelRequest.workspaceId,
            useCaseChannelRequest.uuid
        ).mapLatest {
            it.map { skMessage ->
                skMessage.senderInfo = skLocalDataSourceUsers.getUser(useCaseChannelRequest.workspaceId,skMessage.sender)
                skMessage
            }
        }
    }
}