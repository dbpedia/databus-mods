package org.dbpedia.databus_mods.server.core.controller

import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Optional

import javax.servlet.ServletResponse
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.dbpedia.databus_mods.server.core.config.Defaults
import org.dbpedia.databus_mods.server.core.config.Defaults.{databusFile, modName}
import org.dbpedia.databus_mods.server.core.controller.admin.ModController
import org.dbpedia.databus_mods.server.core.persistence.{Task, TaskStatus}
import org.dbpedia.databus_mods.server.core.service.{DatabusFileService, ModService, TaskService}
import org.dbpedia.databus_mods.server.core.utils.DatabusQueryUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RequestParam, RestController}
import org.springframework.web.servlet.ModelAndView

import scala.collection.JavaConverters._

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

@RestController
@RequestMapping(value = Array("main"))
class MainController(
                      @Value("${tmp.web.base.url}") webBaseUrl: String,
                      @Value("${tmp.sparql.base.url}") sparqlBaseUrl: String,
                      modService: ModService,
                      taskService: TaskService,
                      databusFileService: DatabusFileService) {

  private val log = LoggerFactory.getLogger(classOf[MainController])

  @RequestMapping(value = Array(), method = Array(RequestMethod.GET))
  def main() = {

    val mods = modService.getMods

    val mav = new ModelAndView();
    mav.addObject("iter", mods);
    mav.setViewName("main");
    mav
  }

  @RequestMapping(value = Array("{modName}"), method = Array(RequestMethod.GET))
  def mod(@PathVariable modName: String) = {
    val mav = new ModelAndView();
    mav.addObject("modName", modName);
    mav.setViewName("mod");
    mav
  }


//  @RequestMapping(
//    value = Array("{modName}"),
//    method = Array(RequestMethod.GET)
//  )
//  def query(@PathVariable modName: String,
//            @RequestParam databusID: String,
//            //@RequestParam sha256sum: Optional[String],
//            response: HttpServletResponse): Unit = {
//
//    val possibleMod = modService.get(modName)
//    if (possibleMod.isPresent) {
//
//      // TODO replace with DatabusFile constructure
//      val possibleDatabusFile = DatabusQueryUtil.queryDatabusFileByURI(databusID)
//      if (possibleDatabusFile.isDefined) {
//        // valid Databus file
//        val databusFile = possibleDatabusFile.get
//        databusFileService.add(databusFile)
//        // TODO source in Task entry
//        val task = new Task(databusFile, possibleMod.get())
//        taskService.add(task)
//        tmp(task, response)
//      } else {
//        // databus file not found
//        response.setStatus(400)
//        IOUtils.write("Bad Request", response.getOutputStream, StandardCharsets.UTF_8)
//      }
//    } else {
//      // mod not found
//      response.setStatus(404)
//      IOUtils.write("Mod Not Found", response.getOutputStream, StandardCharsets.UTF_8)
//    }
//  }

  @RequestMapping(
    value = Array("{modName}"),
    method = Array(RequestMethod.POST)
  )
  def create(@PathVariable modName: String,
             @RequestParam databusID: String,
             @RequestParam source: Optional[String],
             @RequestParam sha256sum: Optional[String],
             response: HttpServletResponse): Unit = {

    val possibleMod = modService.get(modName)
    if (possibleMod.isPresent) {

      // TODO replace with DatabusFile constructure
      val possibleDatabusFile = DatabusQueryUtil.queryDatabusFileByURI(databusID)
      if (possibleDatabusFile.isDefined) {
        // valid Databus file
        val databusFile = possibleDatabusFile.get
        if (source.isPresent && sha256sum.isPresent) {
          databusFile.setChecksum(sha256sum.get())
        }
        databusFileService.add(databusFile)
        // TODO source in Task entry
        val task = new Task(databusFile, possibleMod.get())
        taskService.add(task)
        tmp(task, response)
      } else {
        // databus file not found
        response.setStatus(400)
        IOUtils.write("Bad Request", response.getOutputStream, StandardCharsets.UTF_8)
      }
    } else {
      // mod not found
      response.setStatus(404)
      IOUtils.write("Mod Not Found", response.getOutputStream, StandardCharsets.UTF_8)
    }
  }



  @RequestMapping(
    value = Array(
      "{modName}/{publisher}/{group}/{artifact}/{version}/{file}/{extension"),
    method = Array(RequestMethod.GET, RequestMethod.DELETE))
  def get(@PathVariable publisher: String,
                        @PathVariable group: String,
                        @PathVariable artifact: String,
                        @PathVariable version: String,
                        @PathVariable file: String,
                        response: HttpServletResponse): Unit = {

    val mod = modService.get(modName)
    if (mod.isPresent) {


    } else {
      // mod not found
      response.setStatus(404)
      IOUtils.write("Mod Not Found", response.getOutputStream, StandardCharsets.UTF_8)
    }
  }

  private def tmp(task: Task,
                  response: HttpServletResponse): Unit = {

    if (task.getState == TaskStatus.Done.id) {
      println("TODO")
    } else if (task.getState == TaskStatus.Fail.id) {
      response.setStatus(500)
      IOUtils.write("Internal Server Error", response.getOutputStream, StandardCharsets.UTF_8)
    } else {
      response.setStatus(302)
      response.setHeader(
        "Location",
        "/" + task.getMod.getName + new URI(task.getDatabusFile.getDataIdSingleFile).getPath
      )
    }
  }
}