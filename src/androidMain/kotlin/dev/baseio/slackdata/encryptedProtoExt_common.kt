package dev.baseio.slackdata

import dev.baseio.slackdata.protos.SKEncryptedMessage
import dev.baseio.slackdata.protos.KMSKEncryptedMessage
import dev.baseio.slackdata.common.SKByteArrayElement
import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.protos.kmSKEncryptedMessage

actual object ProtoExtensions{
    actual fun KMSKEncryptedMessage.asByteArray(): ByteArray {
        return SKEncryptedMessage
            .newBuilder()
            .addAllFirst(this@asByteArray.firstList.map {
                SKByteArrayElement.newBuilder()
                    .setByte(it.byte)
                    .build()
            })
            .addAllSecond(this@asByteArray.secondList.map {
                SKByteArrayElement.newBuilder()
                    .setByte(it.byte)
                    .build()
            })
            .build()
            .toByteArray()
    }

    actual fun ByteArray.asSKEncryptedMessage(): KMSKEncryptedMessage {
         val with = SKEncryptedMessage.parseFrom(this)
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

