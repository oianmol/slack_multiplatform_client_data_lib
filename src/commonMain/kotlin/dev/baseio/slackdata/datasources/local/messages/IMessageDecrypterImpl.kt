package dev.baseio.slackdata.datasources.local.messages

import dev.baseio.slackdomain.datasources.local.messages.IMessageDecrypter
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.security.RsaEcdsaKeyManagerInstances
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.datasources.IDataDecryptor
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers

class IMessageDecrypterImpl(
    private val skKeyValueData: SKLocalKeyValueSource,
    private val iDataDecrypter: IDataDecryptor,
    private val skLocalDataSourceChannelMembers: SKLocalDataSourceChannelMembers
) : IMessageDecrypter {
    override fun decrypted(message: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage? {
        val myPrivateKey =
            RsaEcdsaKeyManagerInstances.getInstance(skKeyValueData.skUser().email!!).getPrivateKey().encoded

        val channelEncryptedPrivateKey = skLocalDataSourceChannelMembers.getChannelPrivateKeyForMe(
            message.workspaceId,
            message.channelId,
            skKeyValueData.skUser().uuid
        )?.channelEncryptedPrivateKey?.keyBytes

        val decryptedPrivateKeyBytes =
            channelEncryptedPrivateKey?.let { safeChannelEncryptedPrivateKey ->
                iDataDecrypter.decrypt(
                    safeChannelEncryptedPrivateKey,
                    myPrivateKey
                )
            }

        return decryptedPrivateKeyBytes?.let { bytes -> finalMessageAfterDecryption(message, bytes) }
    }


    private fun finalMessageAfterDecryption(
        skLastMessage: DomainLayerMessages.SKMessage,
        privateKeyBytes: ByteArray
    ): DomainLayerMessages.SKMessage {
        var messageFinal = skLastMessage
        runCatching {
            messageFinal =
                messageFinal.copy(
                    decodedMessage = iDataDecrypter.decrypt(messageFinal.message, privateKeyBytes = privateKeyBytes)
                        .decodeToString()
                )
        }
        return messageFinal
    }
}