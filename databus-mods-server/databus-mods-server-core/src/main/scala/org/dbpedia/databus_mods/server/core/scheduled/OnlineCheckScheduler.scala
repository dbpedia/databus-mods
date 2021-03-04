package org.dbpedia.databus_mods.server.core.scheduled

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OnlineCheckScheduler {

  private val log = LoggerFactory.getLogger(classOf[OnlineCheckScheduler])

//  @Scheduled(cron = "0 * * * * ?")
//  def checkFiles(): Unit = {
//    log.info("TODO: impl checkFiles")
//    // in different service to block call from multiple threads this will help to use it for e.g., the init
//  }
}
