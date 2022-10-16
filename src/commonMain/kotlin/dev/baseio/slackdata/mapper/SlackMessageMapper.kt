package dev.baseio.slackdata.mapper

import database.SlackMessage
import dev.baseio.slackdomain.model.message.DomainLayerMessages

class SlackMessageMapper : EntityMapper<DomainLayerMessages.SKMessage, SlackMessage> {
  override fun mapToDomain(entity: SlackMessage): DomainLayerMessages.SKMessage {
    return DomainLayerMessages.SKMessage(
      entity.uuid,
      entity.workspaceId,
      entity.channelId,
      entity.message,
      entity.receiver_,
      entity.sender,
      entity.createdDate,
      entity.modifiedDate,
      isDeleted = entity.isDeleted == 1L,
      isSynced = entity.isSynced == 1L
    )
  }

  override fun mapToData(model: DomainLayerMessages.SKMessage): SlackMessage {
    return SlackMessage(
      model.uuid,
      model.workspaceId,
      model.channelId,
      model.message,
      model.receiver,
      model.sender,
      model.createdDate,
      model.modifiedDate,
      if (model.isDeleted) 1 else 0,
      if (model.isSynced) 1 else 0
    )
  }
}