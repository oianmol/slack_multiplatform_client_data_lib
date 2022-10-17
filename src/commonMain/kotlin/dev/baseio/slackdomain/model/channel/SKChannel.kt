package dev.baseio.slackdomain.model.channel

import kotlinx.datetime.Clock


interface DomainLayerChannels {

  sealed class SKChannel(
    val workspaceId: String,
    val channelId: String,
    var pictureUrl: String? = null,
    var channelName: String? = null
  ) {
    data class SkDMChannel(
      val uuid: String,
      val workId: String,
      var senderId: String,
      var receiverId: String,
      val createdDate: Long = Clock.System.now().toEpochMilliseconds(),
      val modifiedDate: Long = Clock.System.now().toEpochMilliseconds(),
      val deleted: Boolean
    ) : SKChannel(workId, uuid)

    data class SkGroupChannel(
      val uuid: String,
      val workId: String,
      var name: String,
      val createdDate: Long = Clock.System.now().toEpochMilliseconds(),
      val modifiedDate: Long = Clock.System.now().toEpochMilliseconds(),
      var avatarUrl: String?,
      val deleted: Boolean
    ) : SKChannel(workId, uuid, channelName = name, pictureUrl = avatarUrl)
  }

  data class SkChannelMember(
    val uuid: String,
    val workspaceId: String,
    val channelId: String,
    val memberId: String
  )
}
