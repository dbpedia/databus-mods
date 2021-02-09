package org.dbpedia.databus_mods.server.core.controller

import com.fasterxml.jackson.annotation.JsonView
import org.dbpedia.databus_mods.lib.databus.DatabusIdentifier
import org.dbpedia.databus_mods.server.core.service.TaskService
import org.dbpedia.databus_mods.server.core.views.Views
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam, RestController}


@RestController
@RequestMapping(value = Array("tasks"))
class TaskController {

  @Autowired
  private var taskService: TaskService = _

  @JsonView(value = Array(classOf[Views.PublicTaskView]))
  @RequestMapping(value = Array(""), method = Array(RequestMethod.GET))
  def getTasks() = {
    taskService.getTasks
  }

  @RequestMapping(value = Array("add"), method = Array(RequestMethod.POST))
  def addTask(
               @RequestParam databusID: String,
               @RequestParam modName: String) = {
    taskService.addTask(modName, DatabusIdentifier(databusID).get)
  }
}
