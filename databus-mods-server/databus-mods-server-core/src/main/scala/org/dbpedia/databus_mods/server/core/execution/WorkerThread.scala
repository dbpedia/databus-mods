package org.dbpedia.databus_mods.server.core.execution

import java.net.URI
import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingDeque}

import org.apache.http.client.utils.URIBuilder
import org.apache.jena.rdf.model.Model
import org.dbpedia.databus_mods.lib.util.ModApiUtil
import org.dbpedia.databus_mods.server.core.persistence.{Task, TaskStatus, Worker}
import org.dbpedia.databus_mods.server.core.service.{MetadataService, TaskService}
import org.slf4j.LoggerFactory

class WorkerThread(worker: Worker,
                   taskService: TaskService) extends Thread {

  private val log = LoggerFactory.getLogger(classOf[WorkerThread])

  private val shutdown = false

  override def run(): Unit = while (!shutdown) {
    val key = worker.getMod.name
    val queue = taskService.getQueue(key)
    val task: Task = queue.take()
    task.setWorker(worker)
    task.setState(TaskStatus.Wait.id)
    taskService.save(task)
    try {
      val df = task.getDatabusFile
      val databusPath = new URI(df.getDataIdSingleFile).getPath.replaceFirst("^/","")
      val uriBuilder = new URIBuilder(worker.getUrl)
      uriBuilder.addParameter("databusID",df.getDataIdSingleFile)
      uriBuilder.addParameter("sourceURI",df.getDownloadUrl)
      val (rdfByteArray,baseURI) = ModApiUtil.submitAndPoll(uriBuilder.build(),1000)
      if(! rdfByteArray.isEmpty) {
        Singleton.metadataService.add(new MetadataExtension(task,rdfByteArray,baseURI))
      } else {
        throw new Exception("No Metadata Found")
      }
    } catch {
      case e: Exception =>
        task.setState(TaskStatus.Fail.id)
        taskService.save(task)
        e.printStackTrace()
    } finally {
      task.setState(TaskStatus.Done.id)
      taskService.save(task)
    }
  }

  def saveResult(o: Object): Unit =  {

  }

  //  {
  //    log.info(s"starting thread for ${worker.url}")
  //    val queue = taskService.getQueue(worker.mod.name)
  //    while (! shutdown) {
  //      val task = queue.take()
  //      try {
  //        task.setState(TaskStatus.Wait.id)
  //        taskService.save(task)
  //
  //        TaskHandler.submit(worker,task,put = true)
  //
  //        task.setState(TaskStatus.Done.id)
  //        taskService.save(task)
  //      } catch {
  //        case e: Exception =>
  //          e.printStackTrace()
  //          throw new WorkerThreadException(task,worker)
  //      }
  //    }
  //  }
}
