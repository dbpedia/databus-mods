package org.dbpedia.databus_mods.server.core.controller.admin

import com.fasterxml.jackson.annotation.JsonView
import org.dbpedia.databus_mods.server.core.persistence.WorkerRepo
import org.dbpedia.databus_mods.server.core.views.Views
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam, RestController}

@RestController
@RequestMapping(value = Array("admin/workers"))
class WorkerController {

  private val log = LoggerFactory.getLogger(classOf[WorkerController])

  @Autowired private var workerRepository: WorkerRepo = _

  @JsonView(value = Array(classOf[Views.PublicWorkerView]))
  @RequestMapping(value = Array(), method = Array(RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST))
  def workers = {
    log.info("get")
    workerRepository.findAll()
  }
//
//  @RequestMapping(value = Array("delete"), method = Array(RequestMethod.DELETE))
//  def deleteWorker(@RequestParam url: String) = {
//    log.info("delete")
//    val worker = workerRepository.findByUrl(url)
//    if(worker.isPresent) {
//      log.info("if")
//      workerRepository.delete(worker.get())
//    } else
//      log.info("else")
//  }
//
//  @RequestMapping(value = Array("deleteAll"), method = Array(RequestMethod.DELETE))
//  def deleteAll = {
//    log.info("deleteAll")
//    workerRepository.deleteAll()
//  }
}
