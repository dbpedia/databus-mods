package org.dbpedia.databus_mods.server.scheduler

import java.io.{FileInputStream, FileOutputStream}
import java.net.URI
import java.nio.file.{Files, Paths}
import java.util

import better.files.File
import org.apache.http.NameValuePair
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost}
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.jena.query.LabelExistsException
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.graph
import org.apache.jena.graph.NodeFactory
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.apache.jena.riot.system.{StreamRDF, StreamRDFLib, StreamRDFWrapper}
import org.dbpedia.databus_mods.server.database.{DatabusFile, DbFactory, JobStatus}
import org.dbpedia.databus_mods.server.utils.FileDownloader
import org.dbpedia.databus_mods.server.{Config, DatabusFileStatus, LinkConfig, ModConfig}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._


// TODO changed func parameters and now it is ugly
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
      case conf: ModConfig => handleJobs(conf)
      case _ => log.warn("incorrect mod config")
    })
  }

  def handleJobs(modConfig: ModConfig): Unit = {

    val modName: String = modConfig.name
    val accepts: String = modConfig.accepts
    val links2: List[LinkConfig] = modConfig.links.asScala.toList

    // TODO distribute and schedule to multiple links
    val link = links2.head

    val activeDatabusFiles = dbConnection.databusFilesByModNameAndStatus(modName, JobStatus.ACTIVE)
    if (activeDatabusFiles.nonEmpty) {
      val databusFile = activeDatabusFiles.head
      log.debug(s"Mod '$modName' - check <${databusFile.id}>")
      checkActiveJob(databusFile, modConfig)
    } else {
      val openDatabusFiles = dbConnection.databusFilesByModNameAndStatus(modName, JobStatus.OPEN)
      if (openDatabusFiles.nonEmpty) {

        val databusFile = openDatabusFiles.head // TODO possibility to bulk

        // TODO condition is not good enough
        if (databusFile.status == DatabusFileStatus.ACTIVE) {
          log.info(s"Mod '$modName' - send Job <${openDatabusFiles.head.id}>")
          val file = File(File(config.fileCache.volume), databusFile.id)
          val uri = file.uri.toString.replace(config.fileCache.volume, link.fileCache)

          sendNewJob(databusFile, modName, link.api, uri)
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
        dbConnection.updateJobStatus(modName, databusFile.id, JobStatus.ACTIVE)
      case 200 => log.info(s"Mod '$modName' - recovered ${databusFile.id}")
        dbConnection.updateJobStatus(modName, databusFile.id, JobStatus.ACTIVE)
      // TODO
      case 400 => log.error(s"Mod' $modName - failed ${databusFile.id}")
        dbConnection.updateJobStatus(modName, databusFile.id, JobStatus.FAILED)
      case code => log.warn(s"Mod '$modName' - $code failed to send ${databusFile.id}")
    }
  }

  def checkActiveJob(databusFile: DatabusFile, modConfig: ModConfig): Unit = {

    val modName: String = modConfig.name
    val modUri: String = modConfig.links.asScala.head.api
    val link = modConfig.links.asScala.head

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

    val file = FileDownloader.getLocalFile(File(config.fileCache.volume), databusFile)
    val uri = file.uri.toString.replace(config.fileCache.volume, link.fileCache)

    import org.apache.http.client.entity.UrlEncodedFormEntity
    import org.apache.http.message.BasicNameValuePair

    val postParameters = new util.ArrayList[NameValuePair]()

    postParameters.add(new BasicNameValuePair("fileUri", uri))
    post.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"))

    val response = client.execute(post)

    response.getStatusLine.getStatusCode match {
      case 400 =>
        log.warn(s"Mod '$modName' - failed ${databusFile.id}")
        dbConnection.updateJobStatus(modName, databusFile.id, JobStatus.FAILED)
      case 201 =>
        log.warn(s"Mod '$modName' - created ${databusFile.id}")
      case 202 =>
      //        log.info(s"Mod '$modName' - active ${databusFile.id}")
      case 200 =>
        log.info(s"Mod '$modName' - done ${databusFile.id}")
        handleModTTL(modConfig, link, response, databusFile)
        //        val jenaModel = ModelFactory.createDefaultModel()
        //        jenaModel.read(response.getEntity.getContent, null, "TTL")
        //        submitToEndpoint(modConfig.name + "/" + databusFile.id, jenaModel)
        dbConnection.updateJobStatus(modName, databusFile.id, JobStatus.DONE)
    }
  }

  def submitToEndpoint(graphName: String, model: Model): Unit = {
    import virtuoso.jena.driver.VirtDataset
    // TODO conf parameter
    val dataSet = new VirtDataset(
      config.extServer.sparql.databaseUrl,
      config.extServer.sparql.databaseUsr,
      config.extServer.sparql.databasePsw
    )
    try {
      dataSet.addNamedModel(graphName, model)
      dataSet.commit()
    } catch {
      case lee: LabelExistsException =>
        // TODO overwrite?
        log.warn(s"Graph exists - $graphName")
      case e: Throwable => e.printStackTrace()
    }

    dataSet.close()
  }

  def handleModTTL(modConfig: ModConfig, linkConfig: LinkConfig, response: CloseableHttpResponse, databusFile: DatabusFile): Unit = {

    val oldBase = s"file://${linkConfig.localRepo}"
    val modName = modConfig.name
    val newBase = config.extServer.http.baseUrl + modName

    val newFile = File(config.extServer.http.volume) / modName / databusFile.id.split("/").dropRight(1).mkString("/") / "mod.ttl"
    newFile.parent.createDirectories()

    val outputStream = new FileOutputStream(newFile.toJava)
    val rewritten = new BaseRewriteStreamWrapper(StreamRDFLib.writer(outputStream), oldBase, newBase, config, modName, linkConfig)
    println(response)
    RDFDataMgr.parse(rewritten, response.getEntity.getContent, oldBase, Lang.TURTLE)
    response.close()

    val jenaModel = ModelFactory.createDefaultModel()
    jenaModel.read(new FileInputStream(newFile.toJava), null, "TTL")
    submitToEndpoint(modConfig.name + "/" + databusFile.id, jenaModel)
  }
}

class BaseRewriteStreamWrapper(streamRDF: StreamRDF,
                               oldBase: String,
                               newBase: String,
                               config: Config,
                              modName: String,
                               linkConfig: LinkConfig
                              ) extends StreamRDFWrapper(streamRDF) {

  override def triple(triple: graph.Triple): Unit = {

    val isUsed = triple.getPredicate.getURI.equals("http://www.w3.org/ns/prov#generated")

    other.triple(
      graph.Triple.create(
        if (triple.getSubject.isURI && triple.getSubject.getURI.startsWith(oldBase))
          NodeFactory.createURI(triple.getSubject.getURI.replace(oldBase, newBase))
        else
          triple.getSubject,
        triple.getPredicate,
        if (triple.getObject.isURI && triple.getObject.getURI.startsWith(oldBase)) {
          if (isUsed) {
            val oldUri = triple.getObject.getURI
            val link = Paths.get(new URI(oldUri.replace(linkConfig.localRepo, config.extServer.http.volume+s"/$modName")))
            val target = Paths.get(new URI(oldUri.replace(linkConfig.localRepo, linkConfig.mountRepo)))
            Files.createDirectories(link.getParent)
            if (!Files.exists(link))
              Files.copy(target, link)
            // TODO
            //  Files.createSymbolicLink(link,target)
          }
          NodeFactory.createURI(triple.getObject.getURI.replace(oldBase, newBase))
        } else
          triple.getObject
      )
    )
  }
}
