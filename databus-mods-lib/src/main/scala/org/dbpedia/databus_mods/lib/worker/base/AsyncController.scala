package org.dbpedia.databus_mods.lib.worker.base

import java.io.FileInputStream
import java.net.URI
import java.util.Optional

import io.swagger.annotations.{ApiOperation, ApiResponse, ApiResponses, Example, ExampleProperty}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RequestParam, RestController}

@RestController
@RequestMapping(value = Array("/${worker.api.base}"))
class AsyncController {

  private val log = LoggerFactory.getLogger(classOf[AsyncController])

  @Autowired
  private var repo: FileRepository = _

  @Autowired
  private var queue: PeekLBQueue = _

  @RequestMapping(
    value = Array(
      "/${worker.api.query}/{account}/{group}/{artifact}/{version}/{distribution}",
      "/${worker.api.query}/{account}/{group}/{artifact}/{version}/{distribution}/{result}"),
    method = Array(RequestMethod.GET))
  def get(
           @PathVariable account: String,
           @PathVariable group: String,
           @PathVariable artifact: String,
           @PathVariable version: String,
           @PathVariable distribution: String,
           @PathVariable result: Optional[String],
           request: HttpServletRequest,
           response: HttpServletResponse)
  : Unit = {

    import scala.collection.JavaConversions._

    if (queue.map(_.dataIdFilePath).contains(s"$account/$group/$artifact/$version/$distribution")) {
      // is running
      response.setStatus(202)
      response.setHeader("Location", request.getRequestURL.toString)
    } else {
      val possibleFile = {
        if(result.isPresent)
          repo.findFile(s"$account/$group/$artifact/$version/$distribution",result.get())
        else
          repo.findFile(s"$account/$group/$artifact/$version/$distribution")
      }
      if (possibleFile.isDefined) {
        // exists
        response.setStatus(200)
        IOUtils.copy(new FileInputStream(possibleFile.get.toJava), response.getOutputStream)
      } else {
        response.setStatus(404)
      }
    }
  }

  @RequestMapping(
    value = Array("/${worker.api.create}/{account}/{group}/{artifact}/{version}/{distribution}"),
    method = Array(RequestMethod.PUT))
  @ApiOperation(value = "Create or update metadata")
  // TODO
  //  @ApiResponses(value = Array(
  //    new ApiResponse(code = 200, message = "Task result as metadata.ttl",
  //      examples = new Example(
  //        value = Array(new ExampleProperty(
  //          mediaType = "text/turtle",
  //          value = ""
  //        )))),
  //    new ApiResponse(code = 202, message = "Accepted task and pending"),
  //    new ApiResponse(code = 400, message = "Bad Request")
  //  ))
  def put(
           @PathVariable account: String,
           @PathVariable group: String,
           @PathVariable artifact: String,
           @PathVariable version: String,
           @PathVariable distribution: String,
           @RequestParam source: String,
           request: HttpServletRequest,
           response: HttpServletResponse
         ): Unit = {
    response.setStatus(202)
    val task = WorkerTask(account, group, artifact, version, distribution, new URI(source))
    response.setHeader("Location", request.getRequestURL.toString)
    if(! queue.contains(task)) queue.put(task)
  }
}
