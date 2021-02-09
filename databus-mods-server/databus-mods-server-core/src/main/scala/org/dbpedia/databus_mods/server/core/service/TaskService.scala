package org.dbpedia.databus_mods.server.core.service

import java.sql.Timestamp
import java.util.function.Consumer

import org.dbpedia.databus_mods.lib.databus.DatabusIdentifier
import org.dbpedia.databus_mods.server.core.persistence._
import org.springframework.stereotype.Service

import scala.collection.JavaConversions._

@Service
class TaskService(taskRepository: TaskRepository,
                  databusFileRepository: DatabusFileRepository,
                  modRepository: ModRepository) {

  def addTask(modName: String, databusIdentifier: DatabusIdentifier): Unit = {
    var databusFile = databusFileRepository.findByDataIdSingleFileAndChecksum(
      databusIdentifier.id,
      "xyz"
    )

    var isNewDBF = false
    if (null == databusFile) {
      isNewDBF = true
      databusFile = new DatabusFile(
        databusIdentifier.id,
        databusIdentifier.publisher,
        databusIdentifier.group,
        databusIdentifier.artifact,
        databusIdentifier.version,
        databusIdentifier.id,
        "xyz",
        new Timestamp(System.currentTimeMillis()))
    }

    var mod = modRepository.findByName(modName)
    if (null == mod) mod = {
      modRepository.save(new Mod(modName, "SELECT * { ?s ?p ?o }"))
    }

    val task = new Task(databusFile, mod)

    if (isNewDBF) {
      databusFile.setTasks(List(task))
      databusFileRepository.save(databusFile)
      taskRepository.save(task)
    }
    else {
      val curDBFTasks = databusFile.getTasks
      if (! curDBFTasks.exists(_.mod.name == modName) ) {
        databusFile.setTasks(curDBFTasks ++ List(task))
        taskRepository.save(task)
      }
    }
  }

  def getTasks() = {
    taskRepository.findAll()
  }
}
