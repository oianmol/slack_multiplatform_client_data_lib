package dev.baseio.slackdata.datasources.remote.auth

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdata.protos.kmSKPushToken
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.remote.auth.SKNetworkSaveFcmToken

class SKNetworkSaveFcmTokenImpl(
    private val iGrpcCalls: IGrpcCalls,
    private val skLocalKeyValueSource: SKLocalKeyValueSource
) : SKNetworkSaveFcmToken {
    override suspend fun save(token: String) {
        iGrpcCalls.saveFcmToken(kmSKPushToken {
            this.userId = skLocalKeyValueSource.skUser().uuid
            this.token = token
            this.platform = 0
        })
    }
}