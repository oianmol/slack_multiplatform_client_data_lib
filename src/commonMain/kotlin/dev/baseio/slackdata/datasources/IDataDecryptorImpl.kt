package dev.baseio.slackdata.datasources

import dev.baseio.security.RsaEcdsaKeyManagerInstances
import dev.baseio.slackdomain.datasources.IDataDecryptor

class IDataDecryptorImpl : IDataDecryptor {
    override fun decrypt(byteArray: ByteArray,chainId:String): ByteArray {
        val keyManager = RsaEcdsaKeyManagerInstances.getInstance(chainId)
        return keyManager.decrypt(byteArray, keyManager.getPrivateKey())
    }
}