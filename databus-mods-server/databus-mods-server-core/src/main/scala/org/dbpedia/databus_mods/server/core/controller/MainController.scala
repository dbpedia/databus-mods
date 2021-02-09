package org.dbpedia.databus_mods.server.core.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}

@RestController
@RequestMapping(value = Array("main"))
class MainController {

  private val log = LoggerFactory.getLogger(classOf[MainController])

  @RequestMapping(value = Array("status"), method = Array(RequestMethod.GET))
  def status(): String = {
    log.info("status")
    """<h1>Databus Mods Server</h1>
      |<ul><li>TODO</li></ul>
      |""".stripMargin
  }
}