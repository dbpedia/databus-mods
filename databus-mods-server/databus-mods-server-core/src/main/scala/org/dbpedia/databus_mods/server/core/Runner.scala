package org.dbpedia.databus_mods.server.core

import java.util.function.Consumer

import org.dbpedia.databus_mods.server.core.config.{ModServerConfig, mods}
import org.dbpedia.databus_mods.server.core.service.ModService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Runner(
              sConf: ModServerConfig,
              modService: ModService) extends CommandLineRunner {

  private val log = LoggerFactory.getLogger(classOf[Runner])


  override def run(args: String*): Unit = {

    modService.deleteAll()

    sConf.mods.forEach(new Consumer[mods.ModConfig] {
      override def accept(t: mods.ModConfig): Unit = {
        modService.addMod(t.name,t.query,t.workers)
        log.info(s"added ${t.name} mod")
      }
    })

  }
}
