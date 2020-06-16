package org.dbpedia.databus.mod.server.scheduler

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * DBpedia Databus update scheduler
 */
@Component
class DatabusUpdate {

  private val log = LoggerFactory.getLogger(classOf[DatabusUpdate])

  /**
   * fixedDelay wait amount of time before next execution
   * fixedRate after fixed time
   * cron
   */
  @Scheduled(fixedRate = 10000)
  def fetchUpdates(): Unit = {

    /**
     * TODO
     */
    log.info("Fetch newest DataIds")
  }
}