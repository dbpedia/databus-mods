package org.dbpedia.databus.mods.worker.springboot.controller

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod}

import java.io.PrintWriter
import java.nio.charset.StandardCharsets

@Controller
abstract class ActivityController {

  /**
   * todo multiple APIs for one application
   */
  private val log = LoggerFactory.getLogger(classOf[ActivityController])

  @RequestMapping(
    value = Array("/activity"),
    method = Array(RequestMethod.GET, RequestMethod.POST))
  def activity(
    request: HttpServletRequest, response: HttpServletResponse
  ): Unit = {

    try {
      handleRequest(request, response)
    } catch {
      case e: Exception =>
        response.setStatus(500)
        val os = response.getOutputStream
        val pw = new PrintWriter(os, true, StandardCharsets.UTF_8)
        e.printStackTrace(pw)
        pw.close()
        os.close()
    }
  }


  @RequestMapping(value = Array("/{jobId}/activity"), method = Array(RequestMethod.GET))
  def job(
    @PathVariable jobId: String,
    request: HttpServletRequest, response: HttpServletResponse
  ): Unit = {
    handleRequest(request, response)
  }

  //  @RequestMapping(
  //    value = Array("{publisher}/{group}/{artifact}/{version}/{file}/activity"),
  //    method = Array(RequestMethod.GET, RequestMethod.POST))
  //  def activity(
  //    @PathVariable publisher: String,
  //    @PathVariable group: String,
  //    @PathVariable artifact: String,
  //    @PathVariable version: String,
  //    @PathVariable file: String,
  //    request: HttpServletRequest, response: HttpServletResponse
  //  ): Unit = {
  //
  //    // TODO
  //    val didPartUri = s"https://databus.dbpedia.org/$publisher/$group/$artifact/$version/$file"
  //
  //    val didPart = Part.apply(didPartUri)
  //    try {
  //      handleRequest(didPart, request, response)
  //    } catch {
  //      case e: Exception =>
  //        response.setStatus(500)
  //        val os = response.getOutputStream
  //        val pw = new PrintWriter(os, true, StandardCharsets.UTF_8)
  //        e.printStackTrace(pw)
  //        pw.close()
  //        os.close()
  //    }
  //  }

  def handleRequest(
    httpServletRequest: HttpServletRequest,
    httpServletResponse: HttpServletResponse
  ): Unit
}
