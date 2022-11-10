package dev.baseio.slackdata.datasources.remote.workspaces

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKCreateWorkspaceRequest
import dev.baseio.slackdata.protos.kmSKUser
import dev.baseio.slackdata.protos.kmSKWorkspace
import dev.baseio.slackdata.protos.kmSlackPublicKey
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkSourceWorkspaces
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.security.getPublicKey
import dev.baseio.slackdata.common.kmSKByteArrayElement
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

class SKNetworkSourceWorkspacesImpl(
    private val grpcCalls: IGrpcCalls,
    private val rsaEcdsaKeyManager: RsaEcdsaKeyManager
) : SKNetworkSourceWorkspaces {
    override suspend fun saveWorkspace(email: String, password: String, domain: String): DomainLayerUsers.SKAuthResult {
        val publicKey = rsaEcdsaKeyManager.getPublicKey()
        return kotlin.run {
            val result = grpcCalls.saveWorkspace(
                kmskCreateWorkspaceRequest(
                    email,
                    password,
                    domain,
                    publicKey.encoded
                )
            )
            DomainLayerUsers.SKAuthResult(
                result.token,
                result.refreshToken,
                DomainLayerUsers.SKStatus(result.status.information, result.status.statusCode)
            )
        }
    }
}

private fun kmskCreateWorkspaceRequest(
    email: String,
    password: String,
    domain: String,
    publicKey: ByteArray
) = kmSKCreateWorkspaceRequest {
    this.user = kmSKAuthUser {
        this.email = email
        this.password = password
        this.user = kmSKUser {
            this.email = email
            this.publicKey = kmSlackPublicKey {
                this.keybytesList.addAll(publicKey.map {
                    kmSKByteArrayElement {
                        this.byte = it.toInt()
                    }
                })
            }
        }
    }
    this.workspace = kmSKWorkspace {
        this.name = domain
    }
}