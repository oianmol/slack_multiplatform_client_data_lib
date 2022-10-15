package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.Flow

class UseCaseFetchMessages(private val skLocalDataSourceMessages: SKLocalDataSourceMessages) {
    operator fun invoke(useCaseChannelRequest: UseCaseChannelRequest): Flow<List<DomainLayerMessages.SKMessage>> {
        return skLocalDataSourceMessages.streamLocalMessages(
            workspaceId = useCaseChannelRequest.workspaceId,
            useCaseChannelRequest.uuid
        )
    }
}