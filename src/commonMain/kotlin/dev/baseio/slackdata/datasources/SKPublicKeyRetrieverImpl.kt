package dev.baseio.slackdata.datasources

import dev.baseio.security.CapillaryInstances
import dev.baseio.slackdata.datasources.remote.channels.toSKUserPublicKey
import dev.baseio.slackdomain.datasources.SKPublicKeyRetriever
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class SKPublicKeyRetrieverImpl : SKPublicKeyRetriever {
    override fun get(uuid: String): DomainLayerUsers.SKUserPublicKey {
        return CapillaryInstances.getInstance(uuid).publicKey().encoded.toSKUserPublicKey()
    }
}