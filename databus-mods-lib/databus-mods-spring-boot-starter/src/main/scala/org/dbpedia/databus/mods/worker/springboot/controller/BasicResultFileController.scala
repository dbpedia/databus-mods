package org.dbpedia.databus.mods.worker.springboot.controller

import org.apache.commons.io.IOUtils
import org.dbpedia.databus.dataid.Part
import org.dbpedia.databus.mods.worker.springboot.service.ResultService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod}

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

@Controller
class BasicResultFileController(resultService: ResultService) {

  @RequestMapping(
    value = Array("{publisher}/{group}/{artifact}/{version}/{file}/{result}"),
    method = Array(RequestMethod.GET, RequestMethod.POST))
  def get(
    @PathVariable publisher: String,
    @PathVariable group: String,
    @PathVariable artifact: String,
    @PathVariable version: String,
    @PathVariable file: String,
    @PathVariable result: String,
    response: HttpServletResponse
  ): Unit = {

    val didPart = Part(s"https://databus.dbpedia.org/$publisher/$group/$artifact/$version/$file")

    resultService.openResultInputStream(didPart, result) match {
      case Some(value) =>
        response.setStatus(200)
        val os = response.getOutputStream
        IOUtils.copy(
          value,
          os
        )
        value.close()
        os.close()
      case None =>
        response.setStatus(404)
    }
  }
}
