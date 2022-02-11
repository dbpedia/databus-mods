package org.dbpedia.databus.mods.core.worker.service

import org.dbpedia.databus.mods.core.worker.execution.{ModExecutor, ModProcessor, ModQueue, ModRequest}
import org.springframework.stereotype.Service

import java.util.concurrent.Executors
import scala.collection.JavaConversions._

@Service
class ExecutionService(process: ModProcessor, fileService: FileService) {

  private val queue = new ModQueue[ModRequest]

  private val poolSize = 1
  private val pool = Executors.newFixedThreadPool(poolSize)
  1.to(poolSize).foreach({
    _ => pool.submit(new ModExecutor(process, queue, fileService))
  })

  def putIfAbsent(item: ModRequest): Unit = synchronized {
    queue.putIfAbsent(item)
  }

  def waitingOrRunning(databusID: String): Boolean = synchronized {
    (queue.getCacheIterator ++ queue.getQueueIterator)
      .exists(request => request.databusID == databusID)
  }
}
