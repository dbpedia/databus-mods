package org.dbpedia.databus_mods.server.scheduler

import java.nio.file.{Files, Path, Paths}

import better.files.File
import org.dbpedia.databus_mods.server.{Config, DatabusFileHandlerQueue, DatabusFileStatus}
import org.dbpedia.databus_mods.server.database.DbFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

@Component
@Scheduled
class CleanUp  @Autowired()(config: Config) {

  private val log = LoggerFactory.getLogger(classOf[CleanUp])

  private val modNames = config.getMods.asScala.map(_.name).toArray
  private val dbConnection = DbFactory.derbyDb(
    config.database.databaseUrl,
    modNames.toList
  )

//  @Scheduled(fixedDelay = 1 * 60 * 1000)
  @Scheduled(fixedDelay = 10 * 1000)
  def fileCache(): Unit = {
    //TODO delete empty folders if older then (because of download step only the older ones)
    getDatafileIdsByDir(config.fileCache.volume).foreach( id => {
      if(! dbConnection.checkOverallStatus(id, modNames).exists(i => i < 2)) {
        log.info(s"delete local file - $id")
        dbConnection.updateDatabusFileStatus(id,DatabusFileStatus.DONE)
        File(File(config.fileCache.volume),id).delete()
        // TODO Bulk decrement?
        DatabusFileHandlerQueue.decrementCurrentTakes()
      }
    })
  }

  def getDatafileIdsByDir(pathString: String): Array[String] = {
    val minDepth = 6
    val maxDepth = minDepth
    val rootPath = Paths.get(pathString)
    val rootPathDepth = rootPath.getNameCount
    Files.walk(rootPath, maxDepth).iterator().asScala
      .filter(e => e.toFile.isFile)
      .filter(e => e.getNameCount - rootPathDepth >= minDepth)
      .map(p => rootPath.relativize(p).toString)
      .toArray
  }
}
