package dev.baseio.slackdata

import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.protos.KMSKEncryptedMessage
import dev.baseio.slackdata.protos.kmSKEncryptedMessage

expect object ProtoExtensions {
    fun KMSKEncryptedMessage.asByteArray(): ByteArray
    fun ByteArray.asSKEncryptedMessageFromBytes(): KMSKEncryptedMessage
}

fun Pair<ByteArray, ByteArray>.toSKEncryptedMessage(): KMSKEncryptedMessage {
    return kmSKEncryptedMessage {
        this.firstList.addAll(this@toSKEncryptedMessage.first.map {
            kmSKByteArrayElement {
                this.byte = it.toInt()
            }
        })
        this.secondList.addAll(this@toSKEncryptedMessage.second.map {
            kmSKByteArrayElement {
                this.byte = it.toInt()
            }
        })
    }
}
