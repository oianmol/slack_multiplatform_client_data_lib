package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseGetChannel(private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels) :
    BaseUseCase<DomainLayerChannels.SKChannel, UseCaseWorkspaceChannelRequest> {
    override suspend fun perform(params: UseCaseWorkspaceChannelRequest): DomainLayerChannels.SKChannel? {
        return skLocalDataSourceReadChannels.getChannel(
            UseCaseWorkspaceChannelRequest(
                workspaceId = params.workspaceId,
                channelId = params.channelId
            )
        )
    }
}

data class UseCaseWorkspaceChannelRequest(val workspaceId: String, val channelId: String, val limit: Int = 20, val offset: Int = 0)