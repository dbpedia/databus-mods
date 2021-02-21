package org.dbpedia.databus_mods.server.core.controller

import com.fasterxml.jackson.annotation.JsonView
import io.swagger.annotations.ApiParam
import javax.servlet.http.HttpServletResponse
import org.checkerframework.common.util.report.qual.ReportUnqualified
import org.dbpedia.databus_mods.server.core.config.Defaults
import org.dbpedia.databus_mods.server.core.execution.TaskQueues
import org.dbpedia.databus_mods.server.core.persistence.DatabusFile
import org.dbpedia.databus_mods.server.core.service.TaskService
import org.dbpedia.databus_mods.server.core.utils.DatabusQueryUtil
import org.dbpedia.databus_mods.server.core.views.Views
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam, RestController}


@RestController
@RequestMapping(value = Array("tasks"))
class TaskController {

  @Autowired
  private var taskService: TaskService = _

  @Autowired
  private var taskQueues: TaskQueues = _

  @JsonView(value = Array(classOf[Views.PublicTaskView]))
  @RequestMapping(value = Array(""), method = Array(RequestMethod.GET))
  def getTasks() = {
    taskService.getTasks()
  }

  @RequestMapping(value = Array("delete"), method = Array(RequestMethod.DELETE))
  def deleteTask(@RequestParam id: String): Unit = {
    taskService.deleteTaskByID(id.toLong)
  }

  @RequestMapping(value = Array("updateAll"), method = Array(RequestMethod.GET))
  def updateAll(): Unit = {
    taskService.update()
  }

  @JsonView(value = Array(classOf[Views.PublicTaskView]))
  @RequestMapping(value = Array("queues"), method = Array(RequestMethod.GET))
  def queues() = {
    taskQueues
  }

  // https://stackoverflow.com/questions/54514014/is-it-possible-to-conditionally-assign-the-value-of-required-in-requestparam
  @RequestMapping(value = Array("add"), method = Array(RequestMethod.POST))
  def addTask(
               @ApiParam(defaultValue = Defaults.databusFile) @RequestParam(required = true) databusFile: String,
               @ApiParam(defaultValue = Defaults.modName) @RequestParam(required = true) modName: String,
               @RequestParam(required = false) downloadURL: String,
               @RequestParam(required = false) sha256sum: String,
               @RequestParam(required = false) issued: String,
               response: HttpServletResponse): Unit = {

    if (null != downloadURL && null != sha256sum && null != issued) {
      response.setStatus(200)
    } else if (null == downloadURL && null == sha256sum && null == issued) {
      response.setStatus(200)
      DatabusQueryUtil.queryDatabusFileByURI(databusFile) match {
        case Some(df) =>

        case None => response.setStatus(400)
      }
    } else {
      response.setStatus(400)
    }
  }
}
