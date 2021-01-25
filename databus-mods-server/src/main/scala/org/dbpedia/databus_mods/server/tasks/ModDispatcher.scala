package org.dbpedia.databus_mods.server.tasks

import java.util.concurrent.LinkedBlockingQueue

import org.dbpedia.databus_mods.server.{Config, ModConfig}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service

@Service
class ModDispatcher extends CommandLineRunner {

  @Autowired
  private var config: Config = _

  @Autowired
  private var jobQueue: Map[String,LinkedBlockingQueue[String]] = _

  override def run(args: String*): Unit = {

    import scala.collection.JavaConversions._
    config.mods.foreach({ mc: ModConfig =>
      new Thread(new ModService(mc,jobQueue(mc.name))).start()
    })
  }
}
