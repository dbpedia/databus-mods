package org.dbpedia.databus.mod.client.scheduler

import org.dbpedia.databus.mod.client.config.ClientConfig
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Heartbeat {

  private val log = LoggerFactory.getLogger(classOf[Heartbeat])


  @Scheduled(fixedRate = 10000)
  def subscribe(): Unit = {

    val config = new ClientConfig
    /**
     * TODO
     */
    //    new RestTemplate().execute(config)
    log.info("send heartbeat to $masterUrl")
  }
}
