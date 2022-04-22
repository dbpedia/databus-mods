package org.dbpedia.databus.mods.worker.springboot.controller

import org.apache.commons.io.IOUtils
import org.dbpedia.databus.dataid.Part
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping

import java.nio.charset.StandardCharsets
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class BasicWorkerApi extends WorkerApi {

  private val log = LoggerFactory.getLogger(classOf[BasicWorkerApi])

  @GetMapping
  override def handleRequest(didPart: Part, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setStatus(404)
    val os = response.getOutputStream
    IOUtils.write("TODO", os, StandardCharsets.UTF_8)
    os.close()
  }
}