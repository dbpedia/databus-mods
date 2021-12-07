package org.dbpedia.databus.mods.worker.springboot.controller

import java.nio.charset.StandardCharsets

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.dbpedia.databus.dataid.SingleFile
import org.springframework.web.bind.annotation.GetMapping

class BasicWorkerApi extends WorkerApi {

  @GetMapping
  override def handleRequest(dbusSF: SingleFile, request: HttpServletRequest, response: HttpServletResponse): Unit = {

    response.setStatus(404)
    val os = response.getOutputStream
    IOUtils.write("TODO",os,StandardCharsets.UTF_8)
    os.close()
  }
}