package org.dbpedia.databus_mods.server.core.controller

import io.swagger.annotations.ApiParam
import org.dbpedia.databus_mods.server.core.demo.ModRepo
import org.dbpedia.databus_mods.server.core.persistence.{ModRepository, TaskRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RequestParam, RestController}

@RestController
@RequestMapping(value = Array("main"))
class MainController {

  @Autowired private var modRepository: ModRepository = _

  @Autowired private var taskRepository: TaskRepository = _

  @RequestMapping(value = Array("mods"), method = Array(RequestMethod.GET))
  def getMods = {
    modRepository.findAll()
  }

//  @RequestMapping(value = Array("mods/$modName"), method = Array(RequestMethod.PUT))
//  def putMod(@PathVariable modName: String) = {
//
//  }
//
//  @RequestMapping(value = Array("tasks"), method = Array(RequestMethod.GET))
//  def getTasks = {
//    taskRepository.findAll()
//  }


//  @RequestMapping(value = Array("mods/update/"))
//  def updateModWorkers(@RequestParam ) = {
//
//  }
}