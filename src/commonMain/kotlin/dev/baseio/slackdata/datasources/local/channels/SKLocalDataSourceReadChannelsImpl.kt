package dev.baseio.slackdata.datasources.local.channels

import database.SkDMChannel
import database.SkPublicChannel
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.LOGGED_IN_USER
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SKLocalDataSourceReadChannelsImpl(
  private val slackChannelDao: SlackDB,
  private val publicChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkPublicChannel>,
  private val directChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkDMChannel>,
  private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider,
) : SKLocalDataSourceReadChannels {

  override fun fetchChannelsOrByName(workspaceId: String, params: String?): Flow<List<DomainLayerChannels.SKChannel>> {
    val flow = kotlin.run {
      params?.takeIf { it.isNotEmpty() }?.let {
        slackChannelDao.slackDBQueries.selectAllPublicChannelsByName(workspaceId, params)
          .asFlow()
          .mapToList(coroutineMainDispatcherProvider.default)

      } ?: run {
        slackChannelDao.slackDBQueries.selectAllPublicChannels(workspaceId).asFlow()
          .mapToList(coroutineMainDispatcherProvider.default)
      }
    }.map { skPublicChannels ->
      skPublicChannels.map { skPublicChannel ->
        publicChannelMapper.mapToDomain(skPublicChannel)
      }
    }

    val flowDMChannels = slackChannelDao.slackDBQueries.selectAllDMChannels(workspaceId).asFlow()
      .mapToList(coroutineMainDispatcherProvider.default).map {
        it.map { skDMChannel ->
          directChannelMapper.mapToDomain(skDMChannel)
        }
      }
    return combine(flow, flowDMChannels) { a, b -> a + b }
  }

  override fun getChannelById(workspaceId: String, uuid: String): DomainLayerChannels.SKChannel? {
    return kotlin.run {
      slackChannelDao.slackDBQueries.selectPublicChannelById(workspaceId, uuid).executeAsOneOrNull()?.let {
        publicChannelMapper.mapToDomain(it)
      }
        ?: slackChannelDao.slackDBQueries.selectDMChannelById(workspaceId, uuid).executeAsOneOrNull()?.let {
          directChannelMapper.mapToDomain(it)
        }
    }
  }

  override suspend fun channelCount(workspaceId: String): Long {
    return slackChannelDao.slackDBQueries.countPublicChannels(workspaceId).executeAsOne()
  }

  override fun fetchChannels(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>> {
    val publicFlow: Flow<List<DomainLayerChannels.SKChannel>> =
      slackChannelDao.slackDBQueries.selectAllPublicChannels(workspaceId).asFlow()
        .mapToList(coroutineMainDispatcherProvider.default)
        .map { skPublicChannels ->
          skPublicChannels.map { skPublicChannel ->
            publicChannelMapper.mapToDomain(skPublicChannel)
          }
        }

    val dmFlow: Flow<List<DomainLayerChannels.SKChannel>> =
      slackChannelDao.slackDBQueries.selectAllDMChannels(workspaceId).asFlow()
        .mapToList(coroutineMainDispatcherProvider.default)
        .map { skDMChannels ->
          skDMChannels.map { skDMChannel ->
            directChannelMapper.mapToDomain(skDMChannel)
          }
        }

    return combine(publicFlow, dmFlow) { a, b ->
      a + b
    }
  }

  override suspend fun getChannel(request: UseCaseWorkspaceChannelRequest): DomainLayerChannels.SKChannel? {
    return getChannelById(request.workspaceId, request.channelId)
  }


}

fun SKKeyValueData.skUser(): DomainLayerUsers.SKUser {
  return Json.decodeFromString(this.get(LOGGED_IN_USER)!!)
}