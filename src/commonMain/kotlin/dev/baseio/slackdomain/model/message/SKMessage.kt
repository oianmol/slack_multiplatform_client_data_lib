package dev.baseio.slackdomain.model.message

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers


interface DomainLayerMessages {
  data class SKMessage(
    val uuid: String,
    val workspaceId: String,
    val channelId: String,
    val message: String,
    val sender: String,
    val createdDate: Long,
    val modifiedDate: Long,
    var isDeleted: Boolean = false,
    var isSynced: Boolean = false
  )

  data class SKLastMessage(
    val channel: DomainLayerChannels.SKChannel,
    val message: SKMessage
  )
}