package dev.baseio.slackdata.datasources

import dev.baseio.security.CapillaryEncryption
import dev.baseio.slackdomain.datasources.IDataEncrypter
import dev.baseio.security.toPublicKey
import dev.baseio.slackdata.ProtoExtensions.asByteArray
import dev.baseio.slackdata.toSKEncryptedMessage

class IDataEncrypterImpl : IDataEncrypter {
    override fun encrypt(
        byteArray: ByteArray,
        publicKeyBytes: ByteArray
    ): ByteArray {
        val encrypted =  CapillaryEncryption.encrypt(
            byteArray, publicKeyBytes.toPublicKey()
        )
        return encrypted.toSKEncryptedMessage().asByteArray()
    }
}