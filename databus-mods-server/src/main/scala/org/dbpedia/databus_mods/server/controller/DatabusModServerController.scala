package org.dbpedia.databus_mods.server.controller

import javax.servlet.http.HttpServletResponse
import org.dbpedia.databus_mods.server.Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}
import sun.misc.IOUtils

@Controller
@RestController
@RequestMapping(Array("api"))
class DatabusModServerController @Autowired()(config: Config) {

  @GetMapping(Array("/"))
  def test(response: HttpServletResponse): Unit = {

    response.setStatus(200)

    val os = response.getOutputStream



  }
}
