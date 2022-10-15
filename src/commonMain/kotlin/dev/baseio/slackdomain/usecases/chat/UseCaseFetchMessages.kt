package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.BaseUseCase
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.withContext

class UseCaseFetchMessages(
    private val SKLocalDataSourceMessages: SKLocalDataSourceMessages,
    private val skNetworkDataSourceMessages: SKNetworkDataSourceMessages,
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) :
    BaseUseCase<List<DomainLayerMessages.SKMessage>, UseCaseChannelRequest> {
    override suspend fun perform(request: UseCaseChannelRequest): List<DomainLayerMessages.SKMessage> {
        return withContext(coroutineDispatcherProvider.io) {
            return@withContext kotlin.runCatching {
                val messages = skNetworkDataSourceMessages.fetchMessages(request)
                messages.map {
                    skLocalDataSourceUsers.saveUser(it.senderInfo) // TODO remove senderInfo from model once we have users stream finalized
                    SKLocalDataSourceMessages.saveMessage(it)
                }
            }.run {
                this.getOrNull()
                    ?: emptyList() // this usecase fetches from network and returns the messages to the consumer after saving
                //TODO inform subscriber for incoming exceptions for a fallback! Should we create Repositories ?
            }
        }
    }
}
