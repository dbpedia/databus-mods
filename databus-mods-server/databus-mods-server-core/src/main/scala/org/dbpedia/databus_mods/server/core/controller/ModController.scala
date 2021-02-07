package org.dbpedia.databus_mods.server.core.controller

import com.fasterxml.jackson.annotation.JsonView
import javax.servlet.http.HttpServletResponse
import org.apache.commons.collections.IteratorUtils
import org.dbpedia.databus_mods.server.core.ModService
import org.dbpedia.databus_mods.server.core.persistence.{Mod, ModRepository, Worker}
import org.dbpedia.databus_mods.server.core.views.Views
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, PostMapping, RequestMapping, RequestMethod, RequestParam, RestController}

import scala.collection.JavaConversions._

@RestController
@RequestMapping(value = Array("mods"))
class ModController {

  @Autowired
  private var modRepository: ModRepository = _

  @Autowired
  private var modService: ModService = _

  @JsonView(value = Array(classOf[Views.PublicModView]))
  @RequestMapping(value = Array(), method = Array(RequestMethod.GET))
  def getMods = {
    modRepository.findAll()
  }

  @RequestMapping(value = Array("{modName}/addWorker"), method = Array(RequestMethod.POST))
  def addWorker(
               @PathVariable modName: String,
               @RequestParam addr: String,
               response: HttpServletResponse) = {

    val mod = modRepository.findByName(modName)
    if( null != mod ) {
      val currentWorker = mod.getWorker
      currentWorker.add(new Worker(mod,addr))
      mod.setWorker(currentWorker)
      modRepository.save(mod)
      response.setStatus(201)
    } else {
      response.setStatus(404)
    }
  }

//  @GetMapping(value = Array(""))
//  def modsList(): java.util.List[Mod] = {
//    IteratorUtils.toList(modRepository.findAll().iterator()).asInstanceOf[java.util.List[Mod]]
//  }
//
//  @PostMapping(value = Array("add/{modName}/{serviceApi}"))
//  def addModServiceApi(
//                        @PathVariable modName: String,
//                        @PathVariable serviceApi: String,
//                        response: HttpServletResponse)
//  : Unit = {
//    modRepository.findByName(modName) match {
//      case mod: Mod =>
//        mod.worker.add(serviceApi)
//        modRepository.save(mod)
//        modService.update(mod)
//        response.setStatus(200)
//      case null =>
//        response.setStatus(500)
//    }
//  }
//
//  @PostMapping(value = Array("remove/{modName}"))
//  def removeMod(): Unit = {
//
//  }
}
