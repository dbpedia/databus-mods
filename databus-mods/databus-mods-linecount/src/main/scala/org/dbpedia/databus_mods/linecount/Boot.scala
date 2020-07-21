package org.dbpedia.databus_mods.linecount

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.ConfigurableApplicationContext


@SpringBootApplication
@EnableAutoConfiguration
class Boot {}

object Boot extends App {

  val app: ConfigurableApplicationContext = SpringApplication.run(classOf[Boot], args: _*)

  val executor: DatabusModExecutor = app.getBean(classOf[DatabusModExecutor])
  new Thread(executor).run()

}
