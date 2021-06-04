package org.dbpedia.databus_mods.server.core.service

import java.util
import java.util.Optional

import org.dbpedia.databus_mods.server.core.execution.{WorkerThreadExceptionHandler, WorkerThreadPool}
import org.dbpedia.databus_mods.server.core.persistence.{Worker, WorkerRepository}
import org.springframework.stereotype.Service


@Service
class WorkerService(
                     workerThreadPool: WorkerThreadPool,
                     workerRepository: WorkerRepository, taskService: TaskService) {

  def add(w: Worker): Unit = {
    val worker = workerRepository.findByUrl(w.getUrl)
    if (worker.isPresent) {
      w.copyOf(worker.get())
    } else {
      workerRepository.save(w)
    }

    val et = new WorkerThreadExceptionHandler(taskService, this)
    workerThreadPool.createAndStart(w, et)
  }

  def update(w: Worker): Unit = {
    // TODO
    throw new UnsupportedOperationException("not implemented yet")
  }

  def remove(w: Worker): Unit = {
    workerRepository.delete(w)
    workerThreadPool.stopWorkerThread(w)
  }

  def get(addr: String) : Optional[Worker] = {
    workerRepository.findByUrl(addr)
  }

  def getAll: util.Iterator[Worker] = {
    workerRepository.findAll().iterator()
  }
}
