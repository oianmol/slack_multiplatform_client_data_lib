package dev.baseio.slackdata

import dev.baseio.slackdata.ProtoExtensions.fromKMSKEncryptedMessage
import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.protos.KMSKEncryptedMessage
import dev.baseio.slackdata.protos.kmSKEncryptedMessage

expect object ProtoExtensions {
    fun KMSKEncryptedMessage.asByteArray(): ByteArray
    fun ByteArray.fromKMSKEncryptedMessage(): KMSKEncryptedMessage
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

fun ByteArray.asEncryptedData(): Pair<ByteArray, ByteArray> {
    val encryptedMessage = fromKMSKEncryptedMessage()
    return Pair(
        encryptedMessage.firstList.map { it.byte.toByte() }.toByteArray(),
        encryptedMessage.secondList.map { it.byte.toByte() }.toByteArray()
    )
}
