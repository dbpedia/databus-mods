package org.dbpedia.databus_mods.server.core

import java.net.{HttpURLConnection, URL}
import java.util.concurrent.{Callable, LinkedBlockingQueue}
import java.util.function.Supplier

import org.apache.commons.io.IOUtils
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.databus_mods.server.core.persistence.Task
import org.slf4j.LoggerFactory

class TaskHandler(task: Task, serviceApi: LinkedBlockingQueue[String]) extends Supplier[Model] {

  private val log = LoggerFactory.getLogger(classOf[TaskHandler])

  override def get(): Model = {

    val apiURL = serviceApi.take()
    println(task.mod.name, task.id, apiURL)
    val model = ModelFactory.createDefaultModel()

    val connectionUrl = new URL(new URL(apiURL), new URL(task.databusFile.getDataIdSingleFile).getPath.drop(1))

    var running = true

    while (running) {
      val connection = connectionUrl.openConnection().asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(0)
      connection.setReadTimeout(0)
      connection.setRequestMethod("GET")
      connection.getResponseCode match {
        case 200 =>
          log.debug(s"mod ${connectionUrl} ${task.databusFile.dataIdSingleFile}")
          try {
            RDFDataMgr.read(model,connection.getInputStream,Lang.TTL)
          } catch {
            case exception: Exception =>
              exception.printStackTrace()
          }
          running = false
        case 202 =>
          log.debug(s"sleep ${task.databusFile.dataIdSingleFile}")
          Thread.sleep(200)
        case 400 | 500 =>
          running = false
      }
    }
    serviceApi.put(apiURL)
    model
  }
}
