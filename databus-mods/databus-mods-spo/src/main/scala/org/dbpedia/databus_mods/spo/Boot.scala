package org.dbpedia.databus_mods.spo

import org.dbpedia.databus_mods.lib._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.stereotype.{Component, Controller}

@SpringBootApplication
@EnableAutoConfiguration
class Boot

object Boot {

  @Configuration
  class DatabusModConfig extends AbcDatabusModConfig

  @Bean
  def getQueue: DatabusModInputQueue = new DatabusModInputQueue

  @Controller
  class DatabusModController @Autowired()(config: DatabusModConfig, queue: DatabusModInputQueue)
    extends AbcDatabusModController(config, queue)

  @Component
  class DatabusModProcessor @Autowired()(config: DatabusModConfig, queue: DatabusModInputQueue)
    extends AbcDatabusModProcessor(config, queue) {
    val processor = new SPOProcessor(config)

    override def process(input: DatabusModInput): Unit = {
      processor.process(input)
    }
  }

  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[Boot], args: _*)
  }
}
