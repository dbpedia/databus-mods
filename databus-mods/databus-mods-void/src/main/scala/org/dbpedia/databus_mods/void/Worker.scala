package org.dbpedia.databus_mods.void

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.annotation.{ComponentScan, Import}

@SpringBootApplication
@Import(value = Array(classOf[org.dbpedia.databus_mods.lib.worker.AsyncWorker]))
class Worker {}

object Worker extends App {
  SpringApplication.run(classOf[Worker], args: _*)
}

