package org.dbpedia.databus.mods.worker.springboot.controller

import java.io.PrintWriter
import java.nio.charset.StandardCharsets

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.dbpedia.databus.dataid.SingleFile
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod}

@Controller
abstract class WorkerApi {

  /**
   * todo multiple APIs for one application
   */

  private val log = LoggerFactory.getLogger(classOf[WorkerApi])


  @RequestMapping(
    value = Array("{publisher}/{group}/{artifact}/{version}/{file}/activity"),
    method = Array(RequestMethod.GET, RequestMethod.POST))
  def activity(
                @PathVariable publisher: String,
                @PathVariable group: String,
                @PathVariable artifact: String,
                @PathVariable version: String,
                @PathVariable file: String,
                request: HttpServletRequest, response: HttpServletResponse): Unit = {

    val dbusSF = SingleFile.apply(publisher, group, artifact, version, file)
    try {
      handleRequest(dbusSF, request, response)
    } catch {
      case e: Exception =>
        response.setStatus(500)
        val os = response.getOutputStream
        val pw = new PrintWriter(os,true, StandardCharsets.UTF_8)
        e.printStackTrace(pw)
        pw.close()
        os.close()
    }
  }

  def handleRequest(dbusSF: SingleFile, request: HttpServletRequest, response: HttpServletResponse): Unit
}
