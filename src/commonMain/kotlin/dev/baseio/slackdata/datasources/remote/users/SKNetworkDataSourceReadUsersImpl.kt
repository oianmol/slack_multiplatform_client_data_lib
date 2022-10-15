package dev.baseio.slackdata.datasources.remote.users

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.auth.toSKUser

class SKNetworkDataSourceReadUsersImpl(private val grpcCalls: GrpcCalls) : SKNetworkDataSourceReadUsers {
    override suspend fun fetchUsers(workspaceId: String): List<DomainLayerUsers.SKUser> {
        val users = grpcCalls.getUsersForWorkspaceId(workspaceId)
        return users.usersList.map { it.toSKUser() }
    }
}