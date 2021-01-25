package org.dbpedia.databus_mods.server.core

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Boot

object Boot {

  def main(args: Array[String]): Unit = {

    SpringApplication.run(classOf[Boot], args: _*)
  }
}
