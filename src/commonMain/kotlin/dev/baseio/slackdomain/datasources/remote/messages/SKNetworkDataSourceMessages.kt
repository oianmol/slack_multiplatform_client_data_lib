package dev.baseio.slackdomain.datasources.remote.messages

import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.Flow

interface SKNetworkDataSourceMessages {
    suspend fun sendMessage(params: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage
    suspend fun fetchMessages(request: UseCaseChannelRequest): Result<List<DomainLayerMessages.SKMessage>>
    fun registerChangeInMessages(request: UseCaseChannelRequest): Flow<Pair<DomainLayerMessages.SKMessage?, DomainLayerMessages.SKMessage?>>
}