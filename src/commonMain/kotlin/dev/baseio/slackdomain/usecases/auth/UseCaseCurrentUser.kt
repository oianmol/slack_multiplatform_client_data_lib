package dev.baseio.slackdomain.usecases.auth

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKUser

class UseCaseCurrentUser(private val grpcCalls: GrpcCalls) {
  suspend operator fun invoke(): Result<KMSKUser> {
   return kotlin.runCatching {
      grpcCalls.currentLoggedInUser()
    }
  }
}
