package dev.baseio.slackdomain.datasources.local.channels

import com.squareup.sqldelight.Query
import database.SlackChannelMember
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceChannelMembers {
  suspend fun save(members: List<DomainLayerChannels.SkChannelMember>)
  fun get(workspaceId: String, channelId: String): Flow<List<DomainLayerChannels.SkChannelMember>>
}