package dev.baseio.slackdomain.usecases.auth

import dev.baseio.database.SlackDB
import dev.baseio.slackdata.SKKeyValueData

class UseCaseLogout(
    private val skKeyValueData: SKKeyValueData,
    private val slackChannelDao: SlackDB,
) {
    operator fun invoke() {
        skKeyValueData.clear()
        with(slackChannelDao.slackDBQueries) {
            deleteAllMessages()
            deleteAllUsers()
            deleteSlackWorkspaces()
            deleteChannelMembers()
            deleteAllDMChannels()
            deleteAllPublicChannels()
        }
    }
}