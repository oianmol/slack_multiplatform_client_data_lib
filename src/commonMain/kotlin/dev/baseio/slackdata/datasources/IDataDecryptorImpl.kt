package dev.baseio.slackdata.datasources

import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.OAEPParameterSpec
import dev.baseio.security.Padding
import dev.baseio.security.toPrivateKey
import dev.baseio.slackdomain.datasources.IDataDecryptor

class IDataDecryptorImpl : IDataDecryptor {
  override fun decrypt(byteArray: ByteArray, privateKeyBytes: ByteArray): ByteArray {
    return HybridRsaUtils.decrypt(
      byteArray, privateKeyBytes.toPrivateKey(), Padding.OAEP, OAEPParameterSpec()
    )
  }

}