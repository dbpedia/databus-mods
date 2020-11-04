package org.dbpedia.databus_mods.lib

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
abstract class AbcDatabusModProcessor(config: AbcDatabusModConfig,
                                      queue: DatabusModInputQueue
                                     ) extends CommandLineRunner {

  override def run(args: String*): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        while(true) {
          val databusModInput = queue.take()
          process(databusModInput)
          queue.removeCurrent(databusModInput.id)
        }
      }
    }).start()
  }

  def process(input: DatabusModInput)
}
