package dev.baseio.slackdata.datasources

import dev.baseio.slackdomain.datasources.IDataEncrypter
import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.OAEPParameterSpec
import dev.baseio.security.Padding
import dev.baseio.security.toPublicKey

class IDataEncrypterImpl : IDataEncrypter {
  override fun encrypt(byteArray: ByteArray, publicKeyBytes: ByteArray): ByteArray {
    return HybridRsaUtils.encrypt(
      byteArray, publicKeyBytes.toPublicKey(), Padding.OAEP, OAEPParameterSpec()
    )
  }
}