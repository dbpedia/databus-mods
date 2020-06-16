package org.dbpedia.databus.mod.server.task

import org.dbpedia.databus.mod.core.DataidQueue
import org.dbpedia.databus.mod.server.config.ServerConfig
import org.slf4j.LoggerFactory

class TaskExecutor {

  class TaskScheduler() extends Runnable {

    private val log = LoggerFactory.getLogger(classOf[TaskScheduler])

    private val config = new ServerConfig

    final override def run(): Unit = {

      try {
        while (true) {
          val dataId = DataidQueue.take()
          log.info(s"process $dataId")
        }
      } catch {
        case e: InterruptedException => Thread.currentThread().interrupt();
      }
    }
  }
}
