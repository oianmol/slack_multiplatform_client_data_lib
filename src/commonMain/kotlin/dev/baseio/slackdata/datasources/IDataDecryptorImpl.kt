package dev.baseio.slackdata.datasources

import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.OAEPParameterSpec
import dev.baseio.security.Padding
import dev.baseio.slackdomain.datasources.IDataDecryptor

class IDataDecryptorImpl : IDataDecryptor {
  override fun decrypt(byteArray: ByteArray, privateKeyBytes: ByteArray): ByteArray {
    val spec = PKCS8EncodedKeySpec(privateKeyBytes)
    val kf = KeyFactory.getInstance("RSA")
    return HybridRsaUtils.decrypt(
      byteArray, kf.generatePrivate(spec), Padding.OAEP, OAEPParameterSpec().oaepParamSpec
    )
  }

}