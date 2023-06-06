package org.dbpedia.databus.mods.worker.springboot.worker.controller.impl

import java.io.FileInputStream
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Optional
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.dbpedia.databus.mods.worker.springboot.worker.execution.ModRequest
import org.dbpedia.databus.mods.worker.springboot.worker.service.{ExecutionService, FileService}
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RequestParam, RestController}

@RestController
@RequestMapping(value = Array("modApi"))
class ModApiControllerAsync(fileService: FileService, executionService: ExecutionService) {

  private val log = LoggerFactory.getLogger(classOf[ModApiControllerAsync])

  @RequestMapping(value = Array(), method = Array(RequestMethod.GET))
  def query(@RequestParam databusID: String,
            request: HttpServletRequest,
            response: HttpServletResponse): Unit = {

    val databusPath = new URI(databusID).getPath
    response.setStatus(302)
    val location = "/modApi" + databusPath
    response.setHeader("Location", location)
  }

  @RequestMapping(value = Array(), method = Array(RequestMethod.POST))
  def create(@RequestParam databusID: String,
             @RequestParam sourceURI: Optional[String],
             request: HttpServletRequest,
             response: HttpServletResponse): Unit = {

    val modRequest = ModRequest(databusID, if (sourceURI.isPresent) sourceURI.get() else databusID)

    if (fileService.findFile(modRequest.databusPath, "error.log").isDefined
      || fileService.findFile(modRequest.databusPath).isDefined) {

    } else {
      executionService.putIfAbsent(modRequest)
    }
    response.setStatus(302)
    val databusPath = new URI(databusID).getPath
    val location = "/modApi" + databusPath + "/metadata.ttl"
    response.setHeader("Location", location)
  }

  @RequestMapping(value = Array(
    "{publisher}/{group}/{artifact}/{version}/{distributionName}/{modFile}"
  ), method = Array(RequestMethod.GET))
  def get(@PathVariable publisher: String,
          @PathVariable group: String,
          @PathVariable artifact: String,
          @PathVariable version: String,
          @PathVariable distributionName: String,
          @PathVariable modFile: String,
          response: HttpServletResponse): Unit = {

    val databusPath = List(publisher, group, artifact, version, distributionName).mkString("/")
    val databusID = "https://databus.dbpedia.org/" + databusPath

    if (fileService.findFile(databusPath, "error.log").isDefined) {
      response.setStatus(500)
      IOUtils.write("Internal Server Error", response.getOutputStream, StandardCharsets.UTF_8)
    } else if (executionService.waitingOrRunning(databusID)) {
      response.setStatus(302)
      val location = "/modApi/" + databusPath + "/" + modFile
      response.setHeader("Location", location)
    } else if (fileService.findFile(databusPath, modFile).isDefined) {
      response.setStatus(200)
      val is = new FileInputStream(fileService.createFile(databusPath,modFile))
      IOUtils.copy(is, response.getOutputStream)
      is.close()
    } else {
      response.setStatus(404)
      IOUtils.write(s"no mod data for ${databusPath}", response.getOutputStream, StandardCharsets.UTF_8)
    }
  }

  @RequestMapping(value = Array(
    "{publisher}/{group}/{artifact}/{version}/{fileName}/{metadataExtension}"
  ), method = Array(RequestMethod.DELETE))
  def delete(): Unit = {
  }
}
