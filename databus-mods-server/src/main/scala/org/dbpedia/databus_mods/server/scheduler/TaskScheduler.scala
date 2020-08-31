package org.dbpedia.databus_mods.server.scheduler

import java.util

import better.files.File
import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.jena.query.LabelExistsException
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.vocabulary.RDF
import org.dbpedia.databus_mods.server.database.{DatabusFile, DbFactory, JobStatus}
import org.dbpedia.databus_mods.server.utils.FileDownloader
import org.dbpedia.databus_mods.server.{Config, DatabusFileStatus, LinkConfig, ModConfig}
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

  private val log = LoggerFactory.getLogger(classOf[TaskScheduler])

  //  @Scheduled(fixedRate = 5 * 60 * 1000)
  @Scheduled(fixedDelay = 1000)
  def cronjob(): Unit = {
    // TODO parallel mods
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

        val databusFile = openDatabusFiles.head // TODO possibility to bulk

        // TODO condition is not good enough
        if(databusFile.status == DatabusFileStatus.ACTIVE) {
          log.info(s"Mod '$modName' - send Job <${openDatabusFiles.head.id}>")
          val file = File(File(config.getVolumes.localRepo), databusFile.id)
          sendNewJob(databusFile,modName,links.head.api, file.uri.toString)
        } else {
          log.info(s"Mod '$modName' - wait for download <${openDatabusFiles.head.id}>")
          /*
          only if all downloaded files relate to different
          Thread.sleep(10*1000)
           */
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
        log.warn(s"Mod '$modName' - failed ${databusFile.id}")
        dbConnection.updateJobStatus(modName, databusFile.id, JobStatus.FAILED)
      case 202 =>
//        log.info(s"Mod '$modName' - active ${databusFile.id}")
      case 200 =>
        log.info(s"Mod '$modName' - done ${databusFile.id}")
        val jenaModel = ModelFactory.createDefaultModel()
        jenaModel.read(response.getEntity.getContent,null,"TTL")
        submitToEndpoint(modName+"/"+databusFile.id,jenaModel)
        dbConnection.updateJobStatus(modName,databusFile.id,JobStatus.DONE)
    }
  }

  def submitToEndpoint(graphName: String, model:Model): Unit = {
    import virtuoso.jena.driver.VirtDataset
    val dataSet = new VirtDataset("jdbc:virtuoso://localhost:1111/charset=UTF-8/", "dba", "myDbaPassword")
    try {
      dataSet.addNamedModel(graphName,model)
      dataSet.commit()
    } catch {
      case lee: LabelExistsException =>
        // TODO overwrite?
        log.warn(s"Graph exists - $graphName")
      case e: Throwable => e.printStackTrace()
    }

    dataSet.close()
  }
}
