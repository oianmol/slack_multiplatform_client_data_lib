package dev.baseio.slackdata.datasources

import dev.baseio.security.CapillaryEncryption
import dev.baseio.slackdomain.datasources.IDataEncrypter
import dev.baseio.security.toPublicKey

class IDataEncrypterImpl : IDataEncrypter {
  override fun encrypt(byteArray: ByteArray, publicKeyBytes: ByteArray): ByteArray {
    return CapillaryEncryption.encrypt(
      byteArray, publicKeyBytes.toPublicKey()
    )
  }
}