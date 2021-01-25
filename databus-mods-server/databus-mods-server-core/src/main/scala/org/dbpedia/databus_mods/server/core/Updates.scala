package org.dbpedia.databus_mods.server.core

import org.dbpedia.databus_mods.server.core.persistence._
import org.dbpedia.databus_mods.server.core.utils.DatabusQueryUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._

@Component
class Updates {

  private val log = LoggerFactory.getLogger(classOf[Updates])

  @Autowired
  private var config: Config = _

  @Autowired
  private var modService: ModService = _

  @Autowired
  private var databusFileRepository: DatabusFileRepository = _

  @Autowired
  private var modRepository: ModRepository = _

  @Scheduled(fixedRate = 10 * 1000)
  def fetchUpdates(): Unit = {

    log.info("start update database")

    modRepository.findAll().foreach(mod => {
      DatabusQueryUtil.getUpdates(mod.query).foreach(databusFile => {

        val df = databusFileRepository.findByDataIdSingleFileAndChecksum(databusFile.getDataIdSingleFile, databusFile.checksum)

        if (df == null) {
          val task = new Task(databusFile, mod)
          task.setState(Status.Open.id)
          databusFile.getTasks.add(task)
          databusFileRepository.save(databusFile)
        } else if (!df.getTasks.exists(_.mod.name == mod.name)) {
          val task = new Task(df, mod)
          task.setState(Status.Open.id)
          df.getTasks.add(task)
          databusFileRepository.save(df)
        }
      })
    })
    modService.notifyDispatcher()

    log.info("done update database")
  }
}
