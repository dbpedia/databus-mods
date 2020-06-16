package org.dbpedia.databus.mod.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Server {

}

object Server {

  def main(args: Array[String]): Unit = {

    SpringApplication.run(classOf[Server], args: _*)
  }
}