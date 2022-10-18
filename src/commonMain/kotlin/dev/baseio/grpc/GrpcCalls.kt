package dev.baseio.grpc


import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.protos.*
import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMChannel
import kotlinx.coroutines.flow.Flow
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMMetadata


class GrpcCalls(
  private val address: String = "localhost",
  private val port: Int = 17600,
  private val skKeyValueData: SKKeyValueData
) {
  companion object {
    const val AUTHENTICATION_TOKEN_KEY = "Authorization"
  }

  val grpcChannel by lazy {
    KMChannel.Builder
      .forAddress(address, port)
      .usePlaintext()
      .build()
  }

  val workspacesStub by lazy {
    KMWorkspaceServiceStub(grpcChannel)
  }

  val channelsStub by lazy {
    KMChannelsServiceStub(grpcChannel)
  }

  val authStub by lazy {
    KMAuthServiceStub(grpcChannel)
  }

  val usersStub by lazy {
    KMUsersServiceStub(grpcChannel)
  }

  val messagingStub by lazy {
    KMMessagesServiceStub(grpcChannel)
  }

  suspend fun getUsersForWorkspaceId(workspace: String, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKUsers {
    return usersStub.getUsers(kmSKWorkspaceChannelRequest { workspaceId = workspace }, fetchToken(token))
  }

  suspend fun currentLoggedInUser(token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKUser {
    return usersStub.currentLoggedInUser(kmEmpty { }, fetchToken(token))
  }


  suspend fun register(kmskAuthUser: KMSKAuthUser, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKAuthResult {
    return authStub.register(kmskAuthUser, fetchToken(token))
  }

  suspend fun forgotPassword(kmskAuthUser: KMSKAuthUser, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKUser {
    return authStub.forgotPassword(kmskAuthUser, fetchToken(token))
  }

  suspend fun resetPassword(kmskAuthUser: KMSKAuthUser, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKUser {
    return authStub.resetPassword(kmskAuthUser, fetchToken(token))
  }

  suspend fun findWorkspaceByName(name: String, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKWorkspace {
    return workspacesStub.findWorkspaceForName(kmSKFindWorkspacesRequest {
      this.name = name
    }, fetchToken(token))
  }

  suspend fun findWorkspacesForEmail(email: String, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKWorkspaces {
    return workspacesStub.findWorkspacesForEmail(kmSKFindWorkspacesRequest {
      this.email = email
    }, fetchToken(token))
  }

  suspend fun login(kmskAuthUser: KMSKAuthUser): KMSKAuthResult {
    return authStub.login(kmskAuthUser)
  }

  suspend fun getWorkspaces(token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKWorkspaces {
    return workspacesStub.getWorkspaces(kmEmpty { }, fetchToken(token))
  }

  suspend fun saveWorkspace(
    workspace: KMSKCreateWorkspaceRequest,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): KMSKAuthResult {
    return workspacesStub.saveWorkspace(workspace, fetchToken(token))
  }

  suspend fun getPublicChannels(
    workspaceIdentifier: String,
    offset: Int,
    limit: Int,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): KMSKChannels {
    return channelsStub.getAllChannels(kmSKChannelRequest {
      workspaceId = workspaceIdentifier
      this.paged = kmSKPagedRequest {
        this.offset = offset
        this.limit = limit
      }
    }, fetchToken(token))
  }

  suspend fun getAllDMChannels(
    workspaceIdentifier: String,
    offset: Int,
    limit: Int,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): KMSKDMChannels {
    return channelsStub.getAllDMChannels(kmSKChannelRequest {
      workspaceId = workspaceIdentifier
      this.paged = kmSKPagedRequest {
        this.offset = offset
        this.limit = limit
      }
    }, fetchToken(token))
  }

  suspend fun savePublicChannel(kmChannel: KMSKChannel, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKChannel {
    return channelsStub.savePublicChannel(kmChannel, fetchToken(token))
  }

  suspend fun saveDMChannel(kmChannel: KMSKDMChannel, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKDMChannel {
    return channelsStub.saveDMChannel(kmChannel, fetchToken(token))
  }

  fun listenToChangeInMessages(
    workspaceChannelRequest: UseCaseWorkspaceChannelRequest,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): Flow<KMSKMessageChangeSnapshot> {
    return messagingStub.registerChangeInMessage(kmSKWorkspaceChannelRequest {
      workspaceId = workspaceChannelRequest.workspaceId
      channelId = workspaceChannelRequest.channelId
    }, fetchToken(token))
  }

  fun listenToChangeInUsers(
    workspaceId: String,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): Flow<KMSKUserChangeSnapshot> {
    return usersStub.registerChangeInUsers(kmSKWorkspaceChannelRequest {
      this.workspaceId = workspaceId
    }, fetchToken(token))
  }

  fun listenToChangeInChannels(
    workspaceId: String,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): Flow<KMSKChannelChangeSnapshot> {
    return channelsStub.registerChangeInChannels(kmSKChannelRequest {
      this.workspaceId = workspaceId
    }, fetchToken(token))
  }

  fun listenToChangeInDMChannels(
    workspaceId: String,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): Flow<KMSKDMChannelChangeSnapshot> {
    return channelsStub.registerChangeInDMChannels(kmSKChannelRequest {
      this.workspaceId = workspaceId
    }, fetchToken(token))
  }

  suspend fun fetchChannelMembers(
    request: UseCaseWorkspaceChannelRequest, token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): KMSKChannelMembers {
    return channelsStub.channelMembers(kmSKWorkspaceChannelRequest {
      this.workspaceId = request.workspaceId
      this.channelId = request.channelId
    }, fetchToken(token))
  }

  suspend fun inviteUserToChannel(
    userId: String,
    channelId: String,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): KMSKChannelMembers {
    return channelsStub.inviteUserToChannel(kmSKInviteUserChannel {
      this.channelId = channelId
      this.userId = userId
    }, fetchToken(token))
  }

  fun listenToChangeInChannelMembers(
    workspaceId: String,
    memberId:String,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): Flow<KMSKChannelMemberChangeSnapshot> {
    return channelsStub.registerChangeInChannelMembers(kmSKChannelMember {
      this.workspaceId = workspaceId
      this.memberId = memberId
    }, fetchToken(token))
  }

  fun listenToChangeInWorkspace(
    workspaceId: String,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): Flow<KMSKWorkspaceChangeSnapshot> {
    return workspacesStub.registerChangeInWorkspace(kmSKWorkspace {
      this.uuid = workspaceId
    }, fetchToken(token))
  }

  suspend fun fetchMessages(request: UseCaseWorkspaceChannelRequest): KMSKMessages {
    return messagingStub.getMessages(kmSKWorkspaceChannelRequest {
      this.workspaceId = request.workspaceId
      this.channelId = request.channelId
      this.paged = kmSKPagedRequest {
        this.limit = request.limit
        this.offset = request.offset
      }
    })
  }

  suspend fun sendMessage(kmskMessage: KMSKMessage, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKMessage {
    return messagingStub.saveMessage(kmskMessage, fetchToken(token))
  }


  private fun fetchToken(token: String?): KMMetadata {
    return KMMetadata().apply {
      if (token != null) {
        set(AUTHENTICATION_TOKEN_KEY, "Bearer $token")
      }
    }
  }

  fun clearAuth() {
    skKeyValueData.clear()
  }


}

