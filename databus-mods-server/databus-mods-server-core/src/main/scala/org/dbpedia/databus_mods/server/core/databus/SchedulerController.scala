package org.dbpedia.databus_mods.server.core.databus

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RequestMapping, RestController}

@RestController
@RequestMapping(value = Array("/scheduler"))
class SchedulerController {

  @Autowired
  private var apiQueue: ApiQueue = _

  @GetMapping(value = Array("add/{name}"))
  def add(@PathVariable name: String): Unit = {
      apiQueue.add(name)
  }

}
