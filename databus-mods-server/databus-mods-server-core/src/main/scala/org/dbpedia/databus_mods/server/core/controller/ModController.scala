package org.dbpedia.databus_mods.server.core.controller

import javax.servlet.http.HttpServletResponse
import org.apache.commons.collections.IteratorUtils
import org.dbpedia.databus_mods.server.core.ModService
import org.dbpedia.databus_mods.server.core.persistence.{Mod, ModRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, PostMapping, RequestMapping, RestController}

import scala.collection.JavaConversions._

@RestController
@RequestMapping(value = Array("mods"))
class ModController {

  @Autowired
  private var modRepository: ModRepository = _

  @Autowired
  private var modService: ModService = _

  @GetMapping(value = Array(""))
  def modsList(): java.util.List[Mod] = {
    IteratorUtils.toList(modRepository.findAll().iterator()).asInstanceOf[java.util.List[Mod]]
  }

  @PostMapping(value = Array("add/{modName}/{serviceApi}"))
  def addModServiceApi(
                        @PathVariable modName: String,
                        @PathVariable serviceApi: String,
                        response: HttpServletResponse)
  : Unit = {
    modRepository.findByName(modName) match {
      case mod: Mod =>
        mod.getServices.add(serviceApi)
        modRepository.save(mod)
        modService.update(mod)
        response.setStatus(200)
      case null =>
        response.setStatus(500)
    }
  }

  @PostMapping(value = Array("remove/{modName}"))
  def removeMod(): Unit = {

  }
}
