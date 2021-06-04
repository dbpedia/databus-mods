//package org.dbpedia.databus_mods.server.core.execution
//
//import java.net.URL
//import java.sql.Timestamp
//import java.time.Instant
//
//import org.apache.http.client.fluent.Request
//import org.apache.commons.io.IOUtils
//import org.apache.http.HttpEntity
//import org.apache.http.client.utils.URIBuilder
//import org.apache.jena.rdf.model.ModelFactory
//import org.apache.jena.riot.{Lang, RDFDataMgr}
//import org.dbpedia.databus_mods.server.core.persistence.{DatabusFile, Mod, Task, Worker}
//import org.slf4j.LoggerFactory
//
///**
// * TODO to class and open connection once and close once using different approach then rest template?
// */
//object TaskHandler {
//
//  case class TaskResponse()
//
//  private val log = LoggerFactory.getLogger(classOf[TaskHandler])
//
//  def pollData(task: Task, put: Boolean = false): ModMetadata = {
//
//    val df = task.getDatabusFile
//
//    val uriBuilder = new URIBuilder(task.getWorker.getUrl)
//
//    val pathSegments = uriBuilder.getPathSegments
//    pathSegments.remove("")
//    pathSegments.add(df.getPublisher)
//    pathSegments.add(df.getDataIdGroup)
//    pathSegments.add(df.getDataIdArtifact)
//    pathSegments.add(df.getDataIdVersion)
//    pathSegments.add(df.getDataIdSingleFile.split("/").last)
//    uriBuilder.setPathSegments(pathSegments)
//
//    val getUri = uriBuilder.build()
//
//    uriBuilder.addParameter("source", df.getDownloadUrl)
//    val putUri = uriBuilder.build()
//
//    val putResponse = Request.Put(putUri).execute().returnResponse()
//    val putStatusCode = putResponse.getStatusLine.getStatusCode
//
//    var retryAfter = 200
//    var location = putStatusCode match {
//      case 200 | 201 | 204 =>
//        getUri
//      case redirect if 202 == redirect || 300 <= redirect || 400 > redirect =>
//        putResponse.getHeaders("location")(0).getValue
//      case e => throw new UnsupportedOperationException(s"not implemented yet: ${e}")
//    }
//
//
//    var possibleResponse: Option[HttpEntity] = None
//
//    do {
//      val getResponse = Request.Get(getUri).execute().returnResponse()
//      val getStatusCode = getResponse.getStatusLine.getStatusCode
//
//      getStatusCode match {
//        case 200 =>
//          retryAfter = -1
//          possibleResponse = Some(getResponse.getEntity)
//        case redirect if 202 == redirect || 300 <= redirect || 400 > redirect =>
//          Thread.sleep(retryAfter)
//        case e =>
//          throw new UnsupportedOperationException(s"not implemented yet: ${e}")
//      }
//    } while (0 <= retryAfter)
//
//    if (possibleResponse.isDefined) {
//      val model = ModelFactory.createDefaultModel()
//      RDFDataMgr.read(model,possibleResponse.get.getContent,getUri+"/".toString,Lang.TTL)
//      ModMetadata(200,Some(model))
//    } else
//      ModMetadata(500,None)
//  }
//
////  private def workerGet(url: URL) = (String, Int) = {
////    val reRequest.Get(url)
////  }
//
//  private def workerPut(url: URL) = {
//
//  }
//
//  def main(args: Array[String]): Unit = {
//    val mod = new Mod("demo", "")
//    val worker = new Worker(mod, "http://localhost:32771/api/")
//    val databusFile = new DatabusFile(
//      "https://databus.dbpedia.org/dbpedia/databus/databus-data/2019.08.11/databus-data.nt.bz2",
//      "https://databus.dbpedia.org/data/databus/databus-data/2019.08.11/databus-data.nt.bz2",
//      "",
//      Timestamp.from(Instant.now()))
//
//    val task = new Task(databusFile, mod)
//    task.setWorker(worker)
//
//    pollData(task)
//  }
//}
//
///*
//  override def get(): Model = {
//
//    val apiURL = serviceApi.take()
//    println(task.mod.name, task.id, apiURL)
//    val model = ModelFactory.createDefaultModel()
//
//    val connectionUrl = new URL(new URL(apiURL), new URL(task.databusFile.getDataIdSingleFile).getPath.drop(1))
//
//    var running = true
//
//    while (running) {
//      val connection = connectionUrl.openConnection().asInstanceOf[HttpURLConnection]
//      connection.setConnectTimeout(0)
//      connection.setReadTimeout(0)
//      connection.setRequestMethod("GET")
//      connection.getResponseCode match {
//        case 200 =>
//          log.debug(s"mod ${connectionUrl} ${task.databusFile.dataIdSingleFile}")
//          try {
//            RDFDataMgr.read(model,connection.getInputStream,Lang.TTL)
//          } catch {
//            case exception: Exception =>
//              exception.printStackTrace()
//          }
//          running = false
//        case 202 =>
//          log.debug(s"sleep ${task.databusFile.dataIdSingleFile}")
//          Thread.sleep(200)
//        case 400 | 500 =>
//          running = false
//      }
//    }
//    serviceApi.put(apiURL)
//    model
//  }
// */
//
//class TaskHandler