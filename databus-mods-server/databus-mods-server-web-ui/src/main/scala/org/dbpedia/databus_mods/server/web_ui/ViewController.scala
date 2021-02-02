package org.dbpedia.databus_mods.server.web_ui

import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.util.Optional

import io.swagger.annotations.ApiOperation
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RequestParam}

@Controller
@RequestMapping(value = Array("repo"))
class ViewController {

  @Autowired
  var repo: Repo = _

  @RequestMapping(
    value = Array(
      "",
      "{account}",
      "{account}/{group}",
      "{account}/{group}/{artifact}",
      "{account}/{group}/{artifact}/{version}",
      "{account}/{group}/{artifact}/{version}/{distribution}",
      "{account}/{group}/{artifact}/{version}/{distribution}/{result}"),
    method = Array(RequestMethod.GET))
  def put(
           @PathVariable account: Optional[String],
           @PathVariable group: Optional[String],
           @PathVariable artifact: Optional[String],
           @PathVariable version: Optional[String],
           @PathVariable distribution: Optional[String],
           @PathVariable result: Optional[String],
           request: HttpServletRequest,
           response: HttpServletResponse)
  : Unit = {

    if(result.isPresent) {
      val dabuIdPath = s"/${account.get()}/${group.get()}/${artifact.get()}/${version.get()}/${distribution.get()}"
      val file = repo.getFile(dabuIdPath,result.get())
      file match {
        case Some(f) =>
          response.setStatus(200)
          response.setHeader("Content-Type","text/plain")
          IOUtils.copy(new FileInputStream(f), response.getOutputStream)
        case None =>
          response.setStatus(404)
      }
    } else {
      val path = List(account,group,artifact,version,distribution).flatMap({
        part => if (part.isPresent) Some(part.get) else None
      }).mkString("/")
      val html = new StringBuilder("<html><ul>")
      repo.listFiles(path).foreach({
        file =>
          html.append(s"""<li><a href="/repo/$path/${file.getName}">${file.getName}</a></li>""")
      })
      html.append("</ul></html>")
      IOUtils.write(html.toString(),response.getOutputStream, StandardCharsets.UTF_8)
      response.setStatus(200)
    }
  }
}
