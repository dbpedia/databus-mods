package org.dbpedia.databus.mods.worker.springboot.controller

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping

import java.nio.charset.StandardCharsets

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