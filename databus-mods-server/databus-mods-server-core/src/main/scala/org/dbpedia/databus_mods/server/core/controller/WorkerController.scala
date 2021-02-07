package org.dbpedia.databus_mods.server.core.controller

import com.fasterxml.jackson.annotation.JsonView
import org.dbpedia.databus_mods.server.core.persistence.WorkerRepository
import org.dbpedia.databus_mods.server.core.views.Views
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}

@RestController
@RequestMapping(value = Array("worker"))
class WorkerController {

  @Autowired private var workerRepository: WorkerRepository = _

  @JsonView(value = Array(classOf[Views.PublicWorkerView]))
  @RequestMapping(value = Array(), method = Array(RequestMethod.GET))
  def getWorker = {
    workerRepository.findAll()
  }
}
