package org.dbpedia.databus_mods.server.dev

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Debug

object Debug {



  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[Debug], args: _*)
  }
}
