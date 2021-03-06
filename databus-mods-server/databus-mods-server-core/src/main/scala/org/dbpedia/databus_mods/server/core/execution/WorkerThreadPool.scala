package org.dbpedia.databus_mods.server.core.execution

import java.util.concurrent.ConcurrentHashMap

import org.dbpedia.databus_mods.server.core.persistence.Worker
import org.dbpedia.databus_mods.server.core.service.{MetadataService, TaskService, WorkerService}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

// https://howtodoinjava.com/java/multi-threading/java-thread-pool-executor-example/

/**
 * TODO queues
 * TODO rename to something linke TaskExecutionService
 * @param
 */
@Component
class WorkerThreadPool(taskService: TaskService, metadataService: MetadataService) {

  Singleton.taskService = taskService
  Singleton.metadataService = metadataService

  private val log = LoggerFactory.getLogger(classOf[WorkerThreadPool])

  private val pool = new ConcurrentHashMap[String,WorkerThread]()

  def createAndStart(worker: Worker, et: WorkerThreadExceptionHandler): Unit = {

    val wt = new WorkerThread(worker,taskService)
    wt.setUncaughtExceptionHandler(et)

    val pKey = worker.url
    pool.put(pKey,wt)
    wt.start()

    log.info("started "+worker.getMod.getName+" "+worker.getUrl)

  }

  def stopWorkerThread(worker: Worker): Unit = {
    // TODO
    throw new UnsupportedOperationException("not implemented yet")
  }

  def updateWorkerThread(worker: Worker): Unit = {
    val pKey = worker.mod.name
    pool.get(pKey)
  }
}
