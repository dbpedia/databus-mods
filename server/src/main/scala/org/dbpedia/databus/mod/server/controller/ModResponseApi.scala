package org.dbpedia.databus.mod.server.controller

import javax.servlet.http.HttpServletResponse
import org.dbpedia.databus.mod.core.DataidQueue
import org.dbpedia.databus.mod.core.notification.subscription.{ModSubRequest, ModSubResponse}
import org.dbpedia.databus.mod.core.notification.updates.ModUpdate
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._

@RestController
@EnableAutoConfiguration
@RequestMapping(name = "base-api", value = Array("/api"))
class ModResponseApi {

  @RequestMapping(method = Array(RequestMethod.GET))
  def doc(response: HttpServletResponse): Unit = {

    response.sendRedirect("/swagger-ui.html")
  }

  @PostMapping(value = Array("file/"))
  def file(@RequestParam fileId: String): ResponseEntity[String] = {
    DataidQueue.put(fileId)
    new ResponseEntity[String]("", HttpStatus.OK)
  }

  @PostMapping(value = Array("file/bulk"))
  def addDataid(@RequestParam dataid: String): ResponseEntity[String] = {
    new ResponseEntity[String]("", HttpStatus.OK)
  }

  @PostMapping(value = Array("mod/subscribe"))
  @ResponseBody
  def modSubscribe(@RequestBody modSubscription: ModSubRequest): ResponseEntity[ModSubResponse] = {
    /**
     * TODO
     */
    new ResponseEntity[ModSubResponse](new ModSubResponse(10 * 60 * 1000), HttpStatus.ACCEPTED)
  }

  @PostMapping(value = Array("mod/update"))
  def modUpdate(@RequestBody modUpdate: ModUpdate): ResponseEntity[String] = {
    /**
     * TODO
     */
    new ResponseEntity[String]("",HttpStatus.OK)
  }
}
