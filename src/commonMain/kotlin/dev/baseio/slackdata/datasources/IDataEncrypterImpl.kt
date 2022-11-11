package dev.baseio.slackdata.datasources

import dev.baseio.slackdomain.datasources.IDataEncrypter
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.RsaEcdsaConstants

class IDataEncrypterImpl : IDataEncrypter {
  override fun encrypt(byteArray: ByteArray, publicKeyBytes: ByteArray): ByteArray {
    return HybridRsaUtils.encrypt(
      byteArray, KeyFactory.getInstance("RSA").generatePublic(
        X509EncodedKeySpec(publicKeyBytes)
      ), RsaEcdsaConstants.Padding.OAEP, RsaEcdsaConstants.OAEP_PARAMETER_SPEC
    )
  }
}