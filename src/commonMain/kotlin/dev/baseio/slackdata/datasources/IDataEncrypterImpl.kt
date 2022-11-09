package dev.baseio.slackdata.datasources

import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.slackdomain.datasources.IDataEncrypter

class IDataEncrypterImpl(private val keyManager: RsaEcdsaKeyManager) : IDataEncrypter {

    init {
        keyManager.rawGenerateKeyPair()
    }

    override fun encrypt(byteArray: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }
}