package dev.baseio.slackdata.datasources

import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.RsaEcdsaConstants
import dev.baseio.slackdomain.datasources.IDataDecryptor
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec

class IDataDecryptorImpl : IDataDecryptor {
  override fun decrypt(byteArray: ByteArray, privateKeyBytes: ByteArray): ByteArray {
    val spec = PKCS8EncodedKeySpec(privateKeyBytes)
    val kf = KeyFactory.getInstance("RSA")
    return HybridRsaUtils.decrypt(
      byteArray, kf.generatePrivate(spec), RsaEcdsaConstants.Padding.OAEP, RsaEcdsaConstants.OAEP_PARAMETER_SPEC
    )
  }

}