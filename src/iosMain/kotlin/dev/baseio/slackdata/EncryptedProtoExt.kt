package dev.baseio.slackdata

import dev.baseio.extensions.*
import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.protos.KMSKEncryptedMessage
import dev.baseio.slackdata.protos.kmSKEncryptedMessage

actual object ProtoExtensions{
    actual fun KMSKEncryptedMessage.asByteArray(): ByteArray {
        return serialize().toByteArrayFromNSData()
    }

    actual fun ByteArray.asSKEncryptedMessage(): KMSKEncryptedMessage {
        val with = KMSKEncryptedMessage.deserialize(this)
        return kmSKEncryptedMessage {
            this.firstList.addAll(with.firstList.map {
                kmSKByteArrayElement {
                    this.byte = it.byte
                }
            })
            this.secondList.addAll(with.secondList.map {
                kmSKByteArrayElement {
                    this.byte = it.byte
                }
            })

        }
    }
}

