package dev.baseio.slackdata.datasources

import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.slackdomain.datasources.IDataDecryptor

class IDataDecryptorImpl(private val keyManager: RsaEcdsaKeyManager) : IDataDecryptor {
    init {
        keyManager.rawGenerateKeyPair()
    }

    override fun decrypt(byteArray: ByteArray): ByteArray {
        return keyManager.decrypt(byteArray, keyManager.getPrivateKey())
    }
}