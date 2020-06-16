package org.dbpedia.databus.mod.client

import java.util

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Client {

}

object Client {

  def main(args: Array[String]): Unit = {

    val app = new SpringApplication(classOf[Client])
    val map =  new util.HashMap[String,AnyRef]()
    map.put("server.port", "8081")
    app.setDefaultProperties(map)
    app.run(args: _*)
  }
}
