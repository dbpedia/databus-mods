package org.dbpedia.databus_mods.server

import java.io.File
import java.net.URL

import org.dbpedia.databus_mods.server.database.{DatabusFile, DbFactory}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters.iterableAsScalaIterableConverter

@Service
class DatabusFileHandler @Autowired()(config: Config) extends Runnable {

  private val log = LoggerFactory.getLogger(classOf[DatabusFileHandler])

  private val dbConnection = DbFactory.derbyDb(
    config.database.databaseUrl,
    config.getMods.asScala.map(_.name).toList
  )

  override def run(): Unit = {

    while (true) {
      val databusFile: DatabusFile = DatabusFileHandlerQueue.take()

      val file = getFile(new File(config.getVolumes.getLocalRepo), databusFile)
      file.getParentFile.mkdirs()

      val url = new URL(databusFile.downloadUrl)

      log.info(s"start download - ${databusFile.downloadUrl}")
      val exitCode =
        sys.process.Process(url)
          /* .#>(Seq("pv", "-f", "-s", url.openConnection.getContentLength.toString, "-N", databusFile.id)) */
          .#>(file).!

      if (exitCode != 0) {
        log.debug(s"failed download - ${databusFile.downloadUrl}")
        dbConnection.updateDatabusFileStatus(databusFile.id, DatabusFileStatus.FAILED)
      } else {
        log.info(s"finished download - ${databusFile.downloadUrl}")
        // TODO check checksum of downloaded file
        dbConnection.updateDatabusFileStatus(databusFile.id, DatabusFileStatus.ACTIVE)
      }
    }
  }

  private def getFile(base: File, databusFile: DatabusFile): File = {
    new File(base, databusFile.id)
  }
}
