package org.dbpedia.databus_mods.server.core.service

import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingDeque}
import java.util.function.Supplier

import org.dbpedia.databus_mods.server.core.execution.TaskQueues
import org.dbpedia.databus_mods.server.core.persistence._
import org.dbpedia.databus_mods.server.core.utils.DatabusQueryUtil
import org.springframework.stereotype.Service

import scala.collection.JavaConversions._

@Service
class TaskService(taskRepository: TaskRepository,
                  databusFileService: DatabusFileService,
                  modRepository: ModRepository,
                  taskQueues: TaskQueues) {

  // TODO init part put all open in queue

  def getQueue(key: String): LinkedBlockingDeque[Task] = {
    taskQueues.getOrCreate(key)
  }

  // TODO to other class
  def update(): Unit = {
    modRepository.findAll().foreach({
      mod =>
        DatabusQueryUtil.getUpdates(mod.query).foreach({
          databusFile =>
            databusFileService.add(databusFile)
            val task = new Task(databusFile, mod)
            add(task)
        })
    })
  }

  def save(t: Task): Unit = {
    //TODO if not exists
    taskRepository.save(t)
  }

  def add(t: Task): Unit = {
    val task = taskRepository.findByDatabusFileIdAndModId(t.databusFile.id, t.mod.id)
    if (task.isPresent) {
      t.copyOf(task.get)
    } else {
      taskRepository.save(t)
    }
    if (t.getState == TaskStatus.Open.id) {
      addToQueue(t)
      // TODO better move to WorkerThread
      //      t.setState(TaskStatus.Wait.id)
      //      taskRepository.save(t)
    }
  }

  private def addToQueue(t: Task): Unit = {
    val key = t.mod.name
    val q = getQueue(key)
    q.putLast(t)
  }

  //  def addTask(modName: String, databusIdentifier: DatabusIdentifier): Unit = {
  //    var databusFile = databusFileRepository.findByDataIdSingleFileAndChecksum(
  //      databusIdentifier.id,
  //      "xyz"
  //    ).get()
  //
  //    var isNewDBF = false
  //    if (null == databusFile) {
  //      isNewDBF = true
  //      databusFile = new DatabusFile(
  //        databusIdentifier.id,
  //        databusIdentifier.publisher,
  //        databusIdentifier.group,
  //        databusIdentifier.artifact,
  //        databusIdentifier.version,
  //        databusIdentifier.id,
  //        "xyz",
  //        new Timestamp(System.currentTimeMillis()))
  //    }
  //
  //    var mod = modRepository.findByName(modName)
  //    if (null == mod) mod = {
  //      modRepository.save(new Mod(modName, "SELECT * { ?s ?p ?o }"))
  //    }
  //
  //    val task = new Task(databusFile, mod)
  //
  //    if (isNewDBF) {
  //      databusFile.setTasks(List(task))
  //      databusFileRepository.save(databusFile)
  //      taskRepository.save(task)
  //    }
  //    else {
  //      val curDBFTasks = databusFile.getTasks
  //      if (! curDBFTasks.exists(_.mod.name == modName) ) {
  //        databusFile.setTasks(curDBFTasks ++ List(task))
  //        taskRepository.save(task)
  //      }
  //    }
  //  }

  def getTasks() = {
    taskRepository.findAll()
  }

  def deleteTaskByID(id: Long) = {
    //    val task = taskRepository.findById(id).
    taskRepository.deleteById(id)
  }
}
