package dev.baseio.slackdomain.usecases.chat


import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.usecases.BaseUseCase
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.*

class UseCaseFetchAndUpdateChangeInMessages(
    private val SKLocalDataSourceMessages: SKLocalDataSourceMessages,
    private val skNetworkDataSourceMessages: SKNetworkDataSourceMessages,
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers
){
    operator fun invoke(request: UseCaseChannelRequest): Flow<Unit> {
        return skNetworkDataSourceMessages.registerChangeInMessages(request)
            .map { messageChangeSnapshot ->
                messageChangeSnapshot.first?.let {
                    skLocalDataSourceUsers.saveUser(it.senderInfo) // TODO remove senderInfo from model once we have users stream finalized
                    SKLocalDataSourceMessages.saveMessage(it)
                }
                messageChangeSnapshot.second?.let {
                    skLocalDataSourceUsers.saveUser(it.senderInfo)
                    SKLocalDataSourceMessages.saveMessage(it)
                }
                Unit
            }
            .catch {
                // TODO tell upstream of exceptions if any
            }
    }
}
