package org.dbpedia.databus.mods.worker.springboot.controller

import org.apache.commons.io.IOUtils
import org.dbpedia.databus.dataid.Part
import org.dbpedia.databus.mods.core.model.ModActivityRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping

import java.nio.charset.StandardCharsets
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class ActivityControllerImpl extends ActivityController {

  private val log = LoggerFactory.getLogger(classOf[ActivityControllerImpl])

  @GetMapping
  override def handleRequest(
    request: HttpServletRequest,
    response: HttpServletResponse
  ): Unit = {
    response.setStatus(404)
    val os = response.getOutputStream
    IOUtils.write("TODO", os, StandardCharsets.UTF_8)
    os.close()
  }
}