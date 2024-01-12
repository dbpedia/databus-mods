package org.dbpedia.databus_mods.server.core.execution

import org.dbpedia.databus.mods.core.worker.api.ModActivityClientHttp
import org.dbpedia.databus_mods.server.core.persistence.{Task, TaskStatus, Worker}
import org.dbpedia.databus_mods.server.core.service.TaskService
import org.slf4j.LoggerFactory
//import org.dbpedia.databus_mods.server.core.execution.Singleton

import java.net.URI

class WorkerThread(worker: Worker,
  taskService: TaskService) extends Thread {

  private val log = LoggerFactory.getLogger(classOf[WorkerThread])

  private val shutdown = false

  private val client = new ModActivityClientHttp

  override def run(): Unit = while (!shutdown) {
    val key = worker.getMod.name
    val queue = taskService.getQueue(key)
    val task: Task = queue.take()
    task.setWorker(worker)
    task.setState(TaskStatus.Wait.id)
    taskService.save(task)
    try {
      val df = task.getDatabusFile
      val endpointUri = new URI(worker.getUrl)
      val dataId = new URI(df.getDataIdSingleFile)

      val activityResult = client.send(endpointUri, dataId, minDelay = 200)
      if (!activityResult.data.isEmpty) {
        Singleton.metadataService.add(new MetadataExtension(task, activityResult.data, new URI(activityResult.baseUri)))
      } else {
        log.error("No Metadata Found")
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

  def saveResult(o: Object): Unit = {

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
