package org.dbpedia.databus_mods.server.scheduler

import java.net.URL

import org.dbpedia.databus_mods.server.{Config, ModConfig}
import org.dbpedia.databus_mods.server.database.{DbFactory, JobStatus}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util

import better.files.File
import org.dbpedia.databus_mods.server.utils.FileDownloader

import scala.collection.JavaConverters._

@Component
@Scheduled
class TaskScheduler @Autowired()(config: Config){

  private val dbConnection = DbFactory.derbyDb(
    config.database.databaseUrl,
    config.getMods.asScala.map(_.name).toList
  )

  private val log = LoggerFactory.getLogger(classOf[DatabusUpdates])

//  @Scheduled(fixedRate = 5 * 60 * 1000)
  @Scheduled(fixedRate = 10 * 1000)
  def cronjob(): Unit = {

    config.mods.asScala.foreach({
      case conf: ModConfig =>
        handleJobs(conf.name, conf.accepts, conf.links.asScala.toList)
      case _ => log.warn("incorrect mod config")
    })
  }

  def handleJobs(modName: String, accepts: String, links: List[String]): Unit = {

    val activeDatabusFiles = dbConnection.databusFilesByModNameAndStatus(modName,JobStatus.ACTIVE)
    if(activeDatabusFiles.nonEmpty) {
      val databusFile = activeDatabusFiles.head
      log.info(s"Mod '$modName' - check <${databusFile.id}>")
//      checkActiveJob(databusFile)
    } else {
      val openDatabusFiles = dbConnection.databusFilesByModNameAndStatus(modName, JobStatus.OPEN)
      if (openDatabusFiles.nonEmpty) {
        log.info(s"Mod '$modName' - send Job <${openDatabusFiles.head.id}>")
        val file = FileDownloader.getLocalFile(File(config.volumes.localRepo),openDatabusFiles.head)
        FileDownloader.toFileIfNotExits(new URL(openDatabusFiles.head.downloadUrl),file)
        //TODO
      } else {
        log.info(s"Mod '$modName' - waiting for new Jobs")
      }
    }
  }

//  def checkActiveJob(databusFile: )
}
