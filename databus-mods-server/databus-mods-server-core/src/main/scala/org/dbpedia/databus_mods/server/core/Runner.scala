package org.dbpedia.databus_mods.server.core

import java.util
import java.util.function.Consumer

import org.dbpedia.databus_mods.server.core.config.{MasterConfig, mods}
import org.dbpedia.databus_mods.server.core.persistence.{Mod, ModRepository, Worker}
import org.dbpedia.databus_mods.server.core.service.{ModService, TaskService, WorkerService}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

@Component
class Runner(
              sConf: MasterConfig,
              modService: ModService,
              taskService: TaskService,
              workerService: WorkerService,
              @Value("${mod-server.schedule.task-updates}") updateDelay: String) extends CommandLineRunner {

  private val log = LoggerFactory.getLogger(classOf[Runner])

  override def run(args: String*): Unit = {

    modService.deleteAll()

    sConf.mods.foreach({
      mc =>
        val mod = new Mod(mc.name, mc.query)
        mod.setSelects(mc.selects)
        modService.add(mod)
        mod.setQuery(mc.query)
        modService.save(mod)
        log.info(s"added ${mc.name} mod")
        mc.workers.foreach({ addr =>
          val worker = new Worker(mod, addr)
          workerService.add(worker)
        })
    })

    // todo add TaskStatus Wait to queue first then TaskStatus Open

    if (updateDelay.toInt > 0 ) {
      log.info("poll updates")
      taskService.update()
      log.info("done updates")
    }

    modService.getMods.foreach({
      mod =>
        log.warn(s"${mod.name} initial queue size " + taskService.getQueue(mod.name).toIterator().size)
    })
  }

  //  def update(): Unit = {
  //    modService.getMods().foreach({
  //      mod =>
  //        DatabusQueryUtil.getUpdates(mod.query).foreach({
  //          databusFile =>
  //            val persistentDatabusFile = databusFileRepository
  //              .findByDataIdSingleFileAndChecksum(databusFile.getDataIdSingleFile, databusFile.checksum)
  //              .orElseGet(new Supplier[DatabusFile] {
  //                override def get(): DatabusFile = {
  //                  databusFileRepository.save(databusFile)
  //                  databusFile
  //                }
  //              })
  //            val task = new Task(persistentDatabusFile, mod)
  //            taskRepository.save(task)
  //
  //          //            val task = if (df == null) {
  //          //              val _task = new Task(databusFile, mod)
  //          //              _task.setState(TaskStatus.Open.id)
  //          //              databusFile.getTasks.add(_task)
  //          //              databusFileRepository.save(databusFile)
  //          //              _task
  //          //            } else if (!df.getTasks.exists(_.mod.name == mod.name)) {
  //          //              val _task = new Task(df, mod)
  //          //              _task.setState(TaskStatus.Open.id)
  //          //              df.getTasks.add(_task)
  //          //              databusFileRepository.save(df)
  //          //              _task
  //          //            }
  //        })
  //    })
  //  }
}
