package org.dbpedia.databus_mods.mimetype

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.{ComponentScan, Import}

@SpringBootApplication
@Import(value = Array(classOf[org.dbpedia.databus_mods.lib.worker.AsyncWorker]))
class Boot {}

object Boot extends App {
  SpringApplication.run(classOf[Boot],args: _*)
}
