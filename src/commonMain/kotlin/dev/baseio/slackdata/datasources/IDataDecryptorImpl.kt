package dev.baseio.slackdata.datasources

import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.RsaEcdsaConstants
import dev.baseio.slackdomain.datasources.IDataDecryptor
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

class IDataDecryptorImpl : IDataDecryptor {
  override fun decrypt(byteArray: ByteArray, privateKeyBytes: ByteArray): ByteArray {
    return HybridRsaUtils.decrypt(
      byteArray, KeyFactory.getInstance("RSA").generatePrivate(
        X509EncodedKeySpec(privateKeyBytes)
      ), RsaEcdsaConstants.Padding.OAEP, RsaEcdsaConstants.OAEP_PARAMETER_SPEC
    )
  }

}