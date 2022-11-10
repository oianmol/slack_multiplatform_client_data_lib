package dev.baseio.slackdata.datasources

import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.slackdomain.datasources.IDataEncrypter

class IDataEncrypterImpl(private val keyManager: RsaEcdsaKeyManager) : IDataEncrypter {
    init {
        keyManager.rawGenerateKeyPair()
    }

    override fun encrypt(byteArray: ByteArray, publicKeyBytes: ByteArray): ByteArray {
        return keyManager.encrypt(byteArray, keyManager.getPublicKey())
    }
}