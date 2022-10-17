package dev.baseio.slackdata.datasources.local.channels

import database.SkDMChannel
import database.SkPublicChannel
import database.SlackMessage
import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelLastMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SlackSKLocalDataSourceChannelLastMessage(
  private val slackChannelDao: SlackDB,
  private val messagesMapper: EntityMapper<DomainLayerMessages.SKMessage, SlackMessage>,
  private val publicChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkPublicChannel>,
  private val dmChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkDMChannel>,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKLocalDataSourceChannelLastMessage {
  override fun fetchChannelsWithLastMessage(workspaceId: String): Flow<List<DomainLayerMessages.SKLastMessage>> {
    val chatPager = slackChannelDao.slackDBQueries.selectLastMessageOfChannel(workspaceId)
      .asFlow()
      .mapToList(coroutineDispatcherProvider.default)
    return chatPager.map {
      it.mapNotNull { channelsWithLastMessage ->
        val channel =
          slackChannelDao.slackDBQueries.selectPublicChannelById(workspaceId, channelsWithLastMessage.channelId)
            .executeAsOneOrNull()
        val message =
          SlackMessage(
            channelsWithLastMessage.uuid,
            channelsWithLastMessage.workspaceId,
            channelsWithLastMessage.channelId,
            channelsWithLastMessage.message,
            channelsWithLastMessage.sender,
            channelsWithLastMessage.createdDate,
            channelsWithLastMessage.modifiedDate,
            channelsWithLastMessage.isDeleted,
            channelsWithLastMessage.isSynced
          )
        channel?.let {
          DomainLayerMessages.SKLastMessage(
            publicChannelMapper.mapToDomain(channel),
            messagesMapper.mapToDomain(message)
          )
        } ?: kotlin.run {
          null
        }
      }
    }
  }
}