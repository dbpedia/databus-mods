package org.dbpedia.databus_mods.server.core.controller.admin

import com.fasterxml.jackson.annotation.JsonView
import org.dbpedia.databus_mods.server.core.persistence.{Task, TaskRepository}
import org.dbpedia.databus_mods.server.core.service.TaskService
import org.dbpedia.databus_mods.server.core.views.Views
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam, RestController}

import java.util
import java.util.Optional


@RestController
@RequestMapping(value = Array("admin/tasks"))
class TaskController {

  @Autowired private var taskService: TaskService = _

  @Autowired private var taskRepository: TaskRepository = _

//  @Autowired
//  private var taskQueues: TaskQueues = _

  @JsonView(value = Array(classOf[Views.PublicTaskView]))
  @RequestMapping(value = Array(""), method = Array(RequestMethod.GET, RequestMethod.DELETE))
  def tasks(@RequestParam state: Optional[String]) = {
    if(state.isPresent) {
      taskRepository.findByState(state.get().toInt)
    } else {
      taskService.getTasks()
    }
  }

//  @JsonView(value = Array(classOf[Views.PublicTaskView]))
//  @RequestMapping(value = Array(""), method = Array(RequestMethod.POST))
//  def create() = {
//
//  }

//  @JsonView(value = Array(classOf[Views.PublicTaskView]))
//  @RequestMapping(value = Array(""), method = Array(RequestMethod.DELETE))
//  def delete() = {
//
//  }


//
//  @RequestMapping(value = Array("delete"), method = Array(RequestMethod.DELETE))
//  def deleteTask(@RequestParam id: String): Unit = {
//    taskService.deleteTaskByID(id.toLong)
//  }
//
//  @RequestMapping(value = Array("updateAll"), method = Array(RequestMethod.GET))
//  def updateAll(): Unit = {
//    taskService.update()
//  }

  @JsonView(value = Array(classOf[Views.PublicTaskView]))
  @RequestMapping(value = Array("queues"), method = Array(RequestMethod.GET))
  def queues() = {
    import scala.collection.JavaConversions._
    val map = new util.HashMap[String,util.Iterator[Task]]()
    taskService.getQueues.foreach(f => {
      val iter = f._2.toIterator()
      map.put(f._1,iter)
    })
    map
  }

//  // https://stackoverflow.com/questions/54514014/is-it-possible-to-conditionally-assign-the-value-of-required-in-requestparam
//  @RequestMapping(value = Array("add"), method = Array(RequestMethod.POST))
//  def addTask(
//               @ApiParam(defaultValue = Defaults.databusFile) @RequestParam(required = true) databusFile: String,
//               @ApiParam(defaultValue = Defaults.modName) @RequestParam(required = true) modName: String,
//               @RequestParam(required = false) downloadURL: String,
//               @RequestParam(required = false) sha256sum: String,
//               @RequestParam(required = false) issued: String,
//               response: HttpServletResponse): Unit = {
//
//    if (null != downloadURL && null != sha256sum && null != issued) {
//      response.setStatus(200)
//    } else if (null == downloadURL && null == sha256sum && null == issued) {
//      response.setStatus(200)
//      DatabusQueryUtil.queryDatabusFileByURI(databusFile) match {
//        case Some(df) =>
//
//        case None => response.setStatus(400)
//      }
//    } else {
//      response.setStatus(400)
//    }
//  }
}
