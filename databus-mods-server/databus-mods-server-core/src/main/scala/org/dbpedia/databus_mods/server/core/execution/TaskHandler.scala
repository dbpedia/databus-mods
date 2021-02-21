package org.dbpedia.databus_mods.server.core.execution

import java.util

import org.dbpedia.databus_mods.server.core.persistence.{Task, Worker}
import org.slf4j.LoggerFactory
import org.springframework.http.{HttpEntity, HttpMethod, HttpStatus}
import org.springframework.web.client.RestTemplate

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

/**
 * TODO to class and open connection once and close once using different approach then rest template?
 */
object TaskHandler {

  private val log = LoggerFactory.getLogger(classOf[TaskHandler])

  def submit(worker: Worker, task: Task, put: Boolean = false): Unit = {

    val df = task.databusFile
    val url = worker.url +
      df.getPublisher + "/" +
      df.getDataIdGroup + "/" +
      df.getDataIdArtifact + "/" +
      df.getDataIdVersion + "/" +
      df.getName

    var inProgress = true
    if (put) {
      log.info(s"put request at $url ")
      val response = putRequest(url, df.getDataIdSingleFile)
      log.info(s"put response at $url wit ${response.getStatusCode}")
    }
    while (inProgress) {
      val response = getRequest(url)
      log.info(s" get ${response.getStatusCode}")
      response.getStatusCode match {
        case HttpStatus.ACCEPTED => Thread.sleep(1000)
        case i: HttpStatus =>
          inProgress = false
      }
    }
  }

  def putRequest(url: String, source: String) = {

    val restTemplate = new RestTemplate()
    val params = new util.HashMap[String, String]()
    params.put("source",source)
    val response = restTemplate.exchange(url + "?source={source}", HttpMethod.PUT, null, classOf[String], params)
    response
  }

  def getRequest(url: String) = {
    val restTemplate = new RestTemplate()
    val params = new util.HashMap[String, String]()
    //    try {
    val response = restTemplate.exchange(url, HttpMethod.GET, null, classOf[String], new util.HashMap[String, String]())
    println(response.getStatusCodeValue)
    response
    //    } catch {
    //      case e: Exception =>
    //        e.printStackTrace()
    //        null
    //    }
  }

}

class TaskHandler