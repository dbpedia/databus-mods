package org.dbpedia.databus_mods.void.gtd

import java.io.FileInputStream
import java.net.{URI, URL}
import java.nio.charset.StandardCharsets
import java.util.Optional

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.dbpedia.databus_mods.void.Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(value = Array("/${worker.api.base}"))
class Controller {

  @Autowired var config: Config = _

  @Autowired var queue: Queue = _

  @Autowired var repo: Repo = _

  @RequestMapping(
    value = Array("/${worker.api.create}/{account}/{group}/{artifact}/{version}/{distribution}"),
    method = Array(RequestMethod.POST, RequestMethod.PUT))
  def create(
              @PathVariable account: String,
              @PathVariable group: String,
              @PathVariable artifact: String,
              @PathVariable version: String,
              @PathVariable distribution: String,
              @RequestParam source: String,
              request: HttpServletRequest,
              response: HttpServletResponse)
  : Unit = {

    val requestUrl = new URL(request.getRequestURL.toString)

    response.setHeader(
      "Location",
      new URL(
        requestUrl,
        s"/${config.worker.api.base}/${config.worker.api.query}" +
          s"/$account/$group/$artifact/$version/$distribution/mod.ttl").toString)

    val task = Task(
      account = account,
      group = group,
      artifact = artifact,
      version = version,
      distribution = distribution,
      source = new URI(source))

    if (queue.contains(task)) {
      response.setStatus(202)
    } else {
      repo.findFile(task.dataIdFilePath) match {
        case Some(file) =>
          request.getMethod match {
            case "PUT" =>
              response.setStatus(202)
              queue.put(task)
            case _ =>
              response.setStatus(302)
          }
        case None =>
          response.setStatus(202)
          queue.put(task)
      }
    }
  }

  @RequestMapping(
    value = Array(
      "/${worker.api.query}",
      "/${worker.api.query}/{account}",
      "/${worker.api.query}/{account}/{group}",
      "/${worker.api.query}/{account}/{group}/{artifact}",
      "/${worker.api.query}/{account}/{group}/{artifact}/{version}",
      "/${worker.api.query}/{account}/{group}/{artifact}/{version}/{distribution}"),
    method = Array(RequestMethod.GET))
  def query302(
                @PathVariable account: Optional[String],
                @PathVariable group: Optional[String],
                @PathVariable artifact: Optional[String],
                @PathVariable version: Optional[String],
                @PathVariable distribution: Optional[String],
                request: HttpServletRequest,
                response: HttpServletResponse) = {

    if(distribution.isPresent ) {
      response.setHeader(
        "Location",
        s"${request.getRequestURL}/mod.ttl")
      response.setStatus(302)
    } else {
      val html = new StringBuilder("<html><ul>")

      val path = List(account,group,artifact,version).flatMap({
        part => if (part.isPresent) Some(part.get) else None
      }).mkString("/")

      repo.listDir(path).foreach({
        file =>
          html.append(s"""<li><a href="${request.getRequestURL}/${file.name}">${file.name}</a></li>""")
      })
      html.append("</ul></html>")
      IOUtils.write(html.toString(),response.getOutputStream, StandardCharsets.UTF_8)
      response.setStatus(200)
    }
  }

  @RequestMapping(
    value = Array(
      "/${worker.api.query}/{account}/{group}/{artifact}/{version}/{distribution}/{metadata}"),
    method = Array(RequestMethod.GET))
  def query(
             @PathVariable account: String,
             @PathVariable group: String,
             @PathVariable artifact: String,
             @PathVariable version: String,
             @PathVariable distribution: String,
             @PathVariable metadata: Optional[String],
             response: HttpServletResponse)
  : Unit = {

    repo.findFile(
      s"${account}/${group}/${artifact}/${version}/${distribution}",
      metadata.orElse("mod.ttl"))
    match {
      case Some(file) =>
        response.setStatus(200)
        IOUtils.copy(new FileInputStream(file.toJava), response.getOutputStream)
      case None =>
        response.setStatus(404)
    }
  }
}
