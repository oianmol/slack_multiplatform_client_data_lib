package dev.baseio.slackdata

import dev.baseio.extensions.*
import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.protos.KMSKEncryptedMessage
import dev.baseio.slackdata.protos.kmSKEncryptedMessage
import kotlinx.cinterop.autoreleasepool

actual object ProtoExtensions {
    actual fun KMSKEncryptedMessage.asByteArray(): ByteArray {
        autoreleasepool {
            return serialize().toByteArrayFromNSData()
        }
    }

    actual fun ByteArray.asSKEncryptedMessageFromBytes(): KMSKEncryptedMessage {
        autoreleasepool {
            val with = KMSKEncryptedMessage.deserialize(this.toData())
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
}

