package org.dbpedia.databus_mods.server.controller

import org.dbpedia.databus_mods.lib.{AbcDatabusModConfig, AbcDatabusModController, AbcDatabusModProcessor, DatabusModInput, DatabusModInputQueue}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.stereotype.{Component, Controller, Service}
import org.springframework.web.bind.annotation.{RequestMapping, RestController}

@Service
class DemoMod {

  @Configuration
  class DemoModConfig extends AbcDatabusModConfig {
    val localRepo = "./demoRepo"
  }

  @Bean
  def getQueue: DatabusModInputQueue = new DatabusModInputQueue

  @Controller
  @RequestMapping(Array("/demo"))
  class DemoMod @Autowired()(config: DemoModConfig, queue: DatabusModInputQueue)
    extends AbcDatabusModController(config, queue)

  @Component
  class DemoModProcessor @Autowired()(config: DemoModConfig, queue: DatabusModInputQueue)
    extends AbcDatabusModProcessor(config, queue) {
    override def process(input: DatabusModInput): Unit = {
      Thread.sleep(10*1000)
    }
  }
}


