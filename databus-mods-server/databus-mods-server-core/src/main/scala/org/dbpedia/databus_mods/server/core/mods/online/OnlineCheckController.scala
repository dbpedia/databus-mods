package org.dbpedia.databus_mods.server.core.mods.online

import com.fasterxml.jackson.annotation.JsonView
import org.dbpedia.databus_mods.server.core.views.Views
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam, RestController}

@RestController
@RequestMapping(value = Array("main/online"))
class OnlineCheckController(onlineCheckService: OnlineCheckService) {

  @RequestMapping(value = Array("config"),method = Array(RequestMethod.POST))
  def setRate(@RequestParam rate: String): Unit = {
    onlineCheckService.setExecutionDelay(rate.toInt)
  }

//  @JsonView(value = Array(classOf[Views.PublicOnlineCheck]))
//  @RequestMapping(value = Array(), method = Array(RequestMethod.GET))
//  def list(): java.util.Iterator[OnlineCheck] = {
//    onlineCheckService.getAll
//  }
//
//  @JsonView(value = Array(classOf[Views.PublicOnlineCheck]))
//  @RequestMapping(value = Array(), method = Array(RequestMethod.POST))
//  def update(): Unit = {
//    onlineCheckService.update()
//  }
}
