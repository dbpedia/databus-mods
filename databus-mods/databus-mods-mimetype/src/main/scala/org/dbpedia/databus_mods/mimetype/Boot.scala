package org.dbpedia.databus_mods.mimetype

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = Array("org.dbpedia.databus_mods.mimetype","org.dbpedia.databus_mods.lib.worker.base"))
class Boot {}

object Boot extends App {



}
