package dev.baseio.slackdata.datasources

import dev.baseio.security.RsaEcdsaKeyManagerInstances
import dev.baseio.slackdomain.datasources.IDataEncrypter

class IDataEncrypterImpl() : IDataEncrypter {
  override fun encrypt(byteArray: ByteArray, publicKeyBytes: ByteArray, chainId: String): ByteArray {
    val keyManager = RsaEcdsaKeyManagerInstances.getInstance(chainId)
    return keyManager.encrypt(byteArray, keyManager.getPublicKeyFromBytes(publicKeyBytes))
  }
}