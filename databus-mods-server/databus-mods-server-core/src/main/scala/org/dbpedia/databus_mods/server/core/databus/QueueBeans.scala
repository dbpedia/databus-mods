package org.dbpedia.databus_mods.server.core.databus

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class QueueBeans {

  @Bean
  def getApiQueue: ApiQueue = {
    val tmp = new ApiQueue()
    tmp.add("a")
    tmp.add("b")
    tmp
  }

}
