package dev.baseio.slackdomain.datasources.remote.users

import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow

interface SKNetworkDataSourceReadUsers {
  suspend fun fetchUsers(workspaceId: String): List<DomainLayerUsers.SKUser>
}