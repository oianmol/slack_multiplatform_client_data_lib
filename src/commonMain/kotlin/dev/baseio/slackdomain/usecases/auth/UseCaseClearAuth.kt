package dev.baseio.slackdomain.usecases.auth

import dev.baseio.slackdata.SKKeyValueData

class UseCaseClearAuth(private val skKeyValueData: SKKeyValueData) {
  operator fun invoke(){
    skKeyValueData.clear()
  }
}