package org.dbpedia.databus_mods.server.core

import org.dbpedia.databus_mods.server.core.persistence.{Mod, ModRepository, TaskRepository, Worker}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

@Component
class InitRunner extends CommandLineRunner {

  @Autowired
  private var config: Config = _

  @Autowired
  private var modRepository: ModRepository = _

  @Autowired
  private var modService: ModService = _

//  @Autowired
//  private var taskRepository: TaskRepository = _

  override def run(args: String*): Unit = {

    config.mods.foreach( modConfig => {
      // TODO saveAndUpdate
      val mod = new Mod(modConfig.name,modConfig.query)
      mod.setWorker(modConfig.worker.map({
        addr => new Worker(mod,addr)}))
      modRepository.save(mod)
      modService.addModDispatcher(mod)
    })
  }
}
