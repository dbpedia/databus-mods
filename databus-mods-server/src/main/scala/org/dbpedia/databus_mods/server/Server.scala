package org.dbpedia.databus_mods.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
class Server {}

object Server extends App {

  val app = new SpringApplication(classOf[Server]).run(args: _*)

  val executor: DatabusFileHandler = app.getBean(classOf[DatabusFileHandler])
  new Thread(executor).run()
}
