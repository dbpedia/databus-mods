package org.dbpedia.databus_mods.server.core.execution

import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingDeque}

import org.dbpedia.databus_mods.server.core.persistence.{Task, TaskStatus, Worker}
import org.dbpedia.databus_mods.server.core.service.TaskService
import org.slf4j.LoggerFactory

class WorkerThread(worker: Worker, taskService: TaskService) extends Thread {

  private val log = LoggerFactory.getLogger(classOf[WorkerThread])
  /*
  TaskQueue in TaskService
  WorkerPool in WorkerService
   */

  private val shutdown = false

  override def run(): Unit = {
    log.info(s"starting thread for ${worker.url}")
    val queue = taskService.getQueue(worker.mod.name)
    while (! shutdown) {
      val task = queue.take()
      try {
        task.setState(TaskStatus.Wait.id)
        taskService.save(task)

        TaskHandler.submit(worker,task,put = true)

        task.setState(TaskStatus.Done.id)
        taskService.save(task)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          throw new WorkerThreadException(task,worker)
      }
    }
  }
}
