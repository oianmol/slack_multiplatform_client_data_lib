package dev.baseio.slackdomain.datasources.local.channels

import database.SkDMChannel
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceReadChannels {
  suspend fun channelCount(workspaceId: String): Long
  suspend fun getChannel(request: UseCaseWorkspaceChannelRequest): DomainLayerChannels.SKChannel?
  fun fetchChannels(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>>
  fun fetchChannelsOrByName(workspaceId: String, params: String?): Flow<List<DomainLayerChannels.SKChannel>>
  fun getChannelById(workspaceId: String,uuid: String):DomainLayerChannels.SKChannel?
  fun getChannelByReceiverId(workspaceId: String, uuid: String): SkDMChannel?
}