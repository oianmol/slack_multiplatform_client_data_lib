package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest

class UseCaseFetchAndSaveMessages(
  private val skLocalDataSourceMessages: SKLocalDataSourceMessages,
  private val skNetworkDataSourceMessages: SKNetworkDataSourceMessages
) {
  suspend operator fun invoke(request: UseCaseWorkspaceChannelRequest) {
    kotlin.runCatching {
      skNetworkDataSourceMessages.fetchMessages(request).getOrThrow().map {
        skLocalDataSourceMessages.saveMessage(it)
      }
    }.run {
      when {
        isFailure -> {
          // TODO update upstream of errors!
        }
      }
    }

  }
}
