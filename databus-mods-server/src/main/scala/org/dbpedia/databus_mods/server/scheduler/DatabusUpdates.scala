package org.dbpedia.databus_mods.server.scheduler

import org.dbpedia.databus_mods.server.{Config, ModConfig}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Scheduled
class DatabusUpdates @Autowired()(config: Config) {

  private val log = LoggerFactory.getLogger(classOf[DatabusUpdates])

  @Scheduled(fixedRate = 5 * 60 * 1000)
  def cronjob(): Unit = {

    import scala.collection.JavaConverters._

    config.mods.asScala.foreach({
      case conf: ModConfig =>
        println(conf.name,conf.accepts,conf.links,conf.query)
      case _ =>
    })

  }
}