package org.dbpedia.databus_mods.server.tasks

import java.util.concurrent.LinkedBlockingQueue

import org.dbpedia.databus_mods.server.{Config, ModConfig}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class ModJobQueue @Autowired()(config: Config) {

  @Bean
  def getModJobQueues: Map[String,LinkedBlockingQueue[String]] = {
    import scala.collection.JavaConversions._
    config.mods.map({mc: ModConfig =>
      mc.name -> new LinkedBlockingQueue[String]()
    }).toMap
  }

}
