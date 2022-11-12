package dev.baseio.slackdata.datasources

import dev.baseio.security.RsaEcdsaKeyManagerInstances
import dev.baseio.slackdata.datasources.remote.channels.toSKUserPublicKey
import dev.baseio.slackdomain.datasources.SKPublicKeyRetriever
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class SKPublicKeyRetrieverImpl : SKPublicKeyRetriever {
    override fun get(uuid: String): DomainLayerUsers.SKUserPublicKey {
        return RsaEcdsaKeyManagerInstances.getInstance(uuid).getPublicKey().encoded.toSKUserPublicKey()
    }
}