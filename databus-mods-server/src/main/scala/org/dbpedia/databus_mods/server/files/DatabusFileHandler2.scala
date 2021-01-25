package org.dbpedia.databus_mods.server.files

import java.util.concurrent.LinkedBlockingQueue

import org.dbpedia.databus_mods.server.{Config, ModConfig}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

/**
 * Implement visitor pattern for different schema (http/s, file, hdfs)
 */
@Service
class DatabusFileHandler2 extends CommandLineRunner{

  @Autowired
  var config: Config = _

  @Autowired
  var jobQueue : Map[String,LinkedBlockingQueue[String]] = _

  override def run(args: String*): Unit = {

    import scala.collection.JavaConversions._

    new Thread(new Runnable {
      override def run(): Unit = {
        config.mods.foreach({mc: ModConfig =>
          jobQueue(mc.name).put("a")
          jobQueue(mc.name).put("b")
          jobQueue(mc.name).put("c")
          Thread.sleep(2000)
          jobQueue(mc.name).put("d")
        })
      }
    }).start()

  }
}
