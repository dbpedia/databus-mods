package org.dbpedia.databus.mods.worker.dummy

import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

import java.nio.charset.StandardCharsets
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

@Controller
class FakeDatabusImpl {

  @RequestMapping(value = Array("/{publisher}/{group}/{version}/{file}"))
  def databusFile(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): Unit = {
    httpServletResponse.setStatus(200)
    val os = httpServletResponse.getOutputStream
    IOUtils.write("Hello World",os,StandardCharsets.UTF_8)
    os.close()
  }
}
