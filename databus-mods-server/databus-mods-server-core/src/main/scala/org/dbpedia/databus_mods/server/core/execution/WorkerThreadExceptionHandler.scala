package org.dbpedia.databus_mods.server.core.execution

import java.lang.Thread.UncaughtExceptionHandler
import java.util.concurrent.LinkedBlockingDeque

import org.dbpedia.databus_mods.server.core.persistence.{Task, TaskStatus, Worker}
import org.dbpedia.databus_mods.server.core.service.{TaskService, WorkerService}

class WorkerThreadExceptionHandler(
                                    taskService: TaskService,
                                    workerService: WorkerService) extends UncaughtExceptionHandler {

  override def uncaughtException(t: Thread, e: Throwable): Unit = {

    t match {
      case wte: WorkerThreadException =>
        val task = wte.task
        task.setState(TaskStatus.Fail.id)
        taskService.save(task)
    }

//    e match {
//      case wte: WorkerThreadException =>
//        // TODO
//      case _ =>
//    }
  }
}
