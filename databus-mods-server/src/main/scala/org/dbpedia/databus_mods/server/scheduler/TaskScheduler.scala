package org.dbpedia.databus_mods.server.scheduler

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.net.URL
import java.util

import better.files.File
import org.apache.commons.io.IOUtils
import org.apache.hadoop.io.file.tfile.ByteArray
import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.Lang
import org.dbpedia.databus_mods.server.database.{DatabusFile, DbFactory, JobStatus}
import org.dbpedia.databus_mods.server.utils.FileDownloader
import org.dbpedia.databus_mods.server.{Config, LinkConfig, ModConfig}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

@Component
@Scheduled
class TaskScheduler @Autowired()(config: Config) {

  private val dbConnection = DbFactory.derbyDb(
    config.database.databaseUrl,
    config.getMods.asScala.map(_.name).toList
  )

  private val log = LoggerFactory.getLogger(classOf[DatabusUpdates])

  //  @Scheduled(fixedRate = 5 * 60 * 1000)
  @Scheduled(fixedDelay = 1000)
  def cronjob(): Unit = {

    config.mods.asScala.foreach({
      case conf: ModConfig =>
        handleJobs(conf.name, conf.accepts, conf.links.asScala.toList)
      case _ => log.warn("incorrect mod config")
    })
  }

  def handleJobs(modName: String, accepts: String, links: List[LinkConfig]): Unit = {

    val activeDatabusFiles = dbConnection.databusFilesByModNameAndStatus(modName, JobStatus.ACTIVE)
    if (activeDatabusFiles.nonEmpty) {
      val databusFile = activeDatabusFiles.head
      log.info(s"Mod '$modName' - check <${databusFile.id}>")
      // TODO distribute to links
      checkActiveJob(databusFile,modName,links.head.api)
    } else {
      val openDatabusFiles = dbConnection.databusFilesByModNameAndStatus(modName, JobStatus.OPEN)
      if (openDatabusFiles.nonEmpty) {
        log.info(s"Mod '$modName' - send Job <${openDatabusFiles.head.id}>")
        val databusFile = openDatabusFiles.head
        val targetFile = FileDownloader.getLocalFile(File(config.volumes.localRepo), databusFile)
        FileDownloader.toFileIfNotExits(new URL(openDatabusFiles.head.downloadUrl), targetFile) match {
          case Some(file: File) =>
            sendNewJob(databusFile,modName,links.head.api, file.uri.toString)
          case None =>
            dbConnection.updateJobStatus(modName,databusFile.id,JobStatus.FAILED)
        }
      } else {
        log.info(s"Mod '$modName' - waiting for new Jobs")
      }
    }
  }

  def sendNewJob(databusFile: DatabusFile, modName: String, modUri: String, fileUri: String): Unit = {
    val client = new DefaultHttpClient()

    val finalUriPath = Array(
      modUri,
      databusFile.publisher,
      databusFile.group,
      databusFile.artifact,
      databusFile.version,
      databusFile.fileName
    ).mkString("/")

    log.info(s"Mod '$modName' - send $finalUriPath")
    val post = new HttpPost(finalUriPath)

    import org.apache.http.client.entity.UrlEncodedFormEntity
    import org.apache.http.message.BasicNameValuePair
    val postParameters = new util.ArrayList[NameValuePair]()
    postParameters.add(new BasicNameValuePair("fileUri", fileUri))
    post.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"))

    val response = client.execute(post)

    response.getStatusLine.getStatusCode match {
      // 201 CREATED
      case 201 => log.info(s"Mod '$modName' - successfully send ${databusFile.id}")
        dbConnection.updateJobStatus(modName,databusFile.id,JobStatus.ACTIVE)
      case 200 => log.info(s"Mod '$modName' - recovered ${databusFile.id}")
        dbConnection.updateJobStatus(modName,databusFile.id,JobStatus.ACTIVE)
      case code => log.warn(s"Mod '$modName' - $code failed to send ${databusFile.id}")
    }
  }

  def checkActiveJob(databusFile: DatabusFile, modName: String, modUri: String): Unit = {

    val client = new DefaultHttpClient()

    val finalUriPath = Array(
      modUri,
      databusFile.publisher,
      databusFile.group,
      databusFile.artifact,
      databusFile.version,
      databusFile.fileName
    ).mkString("/")

    val post = new HttpPost(finalUriPath)

    val file = FileDownloader.getLocalFile(File(config.volumes.localRepo), databusFile)


    import org.apache.http.client.entity.UrlEncodedFormEntity
    import org.apache.http.message.BasicNameValuePair
    val postParameters = new util.ArrayList[NameValuePair]()
    postParameters.add(new BasicNameValuePair("fileUri", file.uri.toString))
    post.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"))

    val response = client.execute(post)

    response.getStatusLine.getStatusCode match {
      case 400 | 201 =>
        dbConnection.updateJobStatus(modName, databusFile.id, JobStatus.FAILED)
        log.warn(s"Mod '$modName' - failed ${databusFile.id}")
      case 202 =>
//        log.info(s"Mod '$modName' - active ${databusFile.id}")
      case 200 =>
        val jenaModel = ModelFactory.createDefaultModel()
        jenaModel.read(response.getEntity.getContent,null,"TTL")
        dbConnection.updateJobStatus(modName,databusFile.id,JobStatus.DONE)
        log.info(s"Mod '$modName' - done ${databusFile.id}")
    }
  }
}
