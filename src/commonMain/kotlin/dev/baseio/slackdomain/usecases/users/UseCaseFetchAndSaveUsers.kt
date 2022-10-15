package dev.baseio.slackdomain.usecases.users

import dev.baseio.slackdomain.datasources.local.users.SKDataSourceCreateUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseFetchAndSaveUsers(
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
    private val skDataSourceCreateUsers: SKDataSourceCreateUsers,
    private val skNetworkDataSourceReadUsers: SKNetworkDataSourceReadUsers
) :
    BaseUseCase<List<DomainLayerUsers.SKUser>, String> {
    override suspend fun perform(params: String): List<DomainLayerUsers.SKUser> {
        return kotlin.runCatching {
            val users = skNetworkDataSourceReadUsers.fetchUsers(workspaceId = params)
            skDataSourceCreateUsers.saveUsers(users)
        }.run {
            // whatever the result be, we expose the local data
            // TODO tell the View about any exception that happened.
            skLocalDataSourceUsers.getUsers(params)
        }
    }
}