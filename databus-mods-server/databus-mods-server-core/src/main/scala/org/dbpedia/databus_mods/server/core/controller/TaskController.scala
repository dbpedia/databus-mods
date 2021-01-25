package org.dbpedia.databus_mods.server.core.controller

import java.util
import java.util.function.Consumer

import org.apache.commons.collections.IteratorUtils
import org.dbpedia.databus_mods.server.core.Config
import org.dbpedia.databus_mods.server.core.persistence.{Task, TaskRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}


@RestController
@RequestMapping(value = Array("tasks"))
class TaskController {

  @Autowired
  private var config: Config = _

  @Autowired
  private var taskRepository: TaskRepository = _

  @GetMapping(value = Array(""))
  def listTasks(): java.util.List[String] = {
    val response = new util.ArrayList[String]()
    taskRepository.findAll().forEach(new Consumer[Task] {
      override def accept(t: Task): Unit = {
        response.add(s"${t.getId},${t.databusFile.getDataIdSingleFile},${t.mod.getName},${t.getState}")
      }
    })
    response
  }

  @GetMapping(value = Array("config"))
  def getConfig(): Config = {
    config
  }
}
