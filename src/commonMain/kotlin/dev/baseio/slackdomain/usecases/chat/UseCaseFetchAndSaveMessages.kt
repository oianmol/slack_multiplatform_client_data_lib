package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.BaseUseCase
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.withContext

class UseCaseFetchAndSaveMessages(
    private val SKLocalDataSourceMessages: SKLocalDataSourceMessages,
    private val skNetworkDataSourceMessages: SKNetworkDataSourceMessages,
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
) {
    suspend operator fun invoke(request: UseCaseChannelRequest) {
        kotlin.runCatching {
            val messages = skNetworkDataSourceMessages.fetchMessages(request).getOrThrow()
            messages.map {
                skLocalDataSourceUsers.saveUser(it.senderInfo) // TODO remove senderInfo from model once we have users stream finalized
                SKLocalDataSourceMessages.saveMessage(it)
            }
        }.run {
            when {
                isFailure -> {
                    // TODO update upstream of errors!
                }
            }
        }

    }
}
