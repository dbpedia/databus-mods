package org.dbpedia.databus_mods.lib

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import io.swagger.annotations._
import javax.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RequestParam}

@Controller
class AbcDatabusModController(config: AbcDatabusModConfig, queue: DatabusModInputQueue) {

  @RequestMapping(value = Array("/{publisher}/{group}/{artifact}/{version}/{fileName}"), method = Array(RequestMethod.POST))
  @ApiOperation(value = "Databus File Task")
  @ApiResponses(value = Array(
    new ApiResponse(code = 200, message = "Task result as metadata.ttl",
      examples = new Example(
        value = Array(new ExampleProperty(
          mediaType = "text/turtle",
          value = "<TODO> <TODO> <TODO> ."
        )))),
    new ApiResponse(code = 202, message = "Accepted task and pending"),
    new ApiResponse(code = 400, message = "Task failed or request is not acceptable")
  ))
  def databusFile
  (@PathVariable publisher: String,
   @PathVariable group: String,
   @PathVariable artifact: String,
   @PathVariable version: String,
   @PathVariable fileName: String,
   @RequestParam fileUri: String,
   response: HttpServletResponse
  ): Unit = {

    DatabusModInput.apply(publisher, group, artifact, version, fileName, fileUri) match {
      case Some(databusModInput) =>
        if (databusModInput.modMetadataFile(config.volumes.localRepo).exists) {
          response.setStatus(200)
          response.setContentType("text/turtle")
          IOUtils.copy(
            databusModInput.modMetadataFile(config.volumes.localRepo).newFileInputStream,
            response.getOutputStream
          )
        } else if (databusModInput.modErrorFile(config.volumes.localRepo).exists) {
          response.setStatus(400)
        } else if (queue.contains(databusModInput.id)) {
          response.setStatus(202)
        } else {
          response.setStatus(201)
          queue.put(databusModInput)
        }
      case _ => response.setStatus(400)
    }
  }

  @RequestMapping(value = Array("/info"), method = Array(RequestMethod.GET))
  def info(response: HttpServletResponse): Unit = {
    val info =
      """{
        |  'query': 'Select * foobar...'
        |  'accepts': 'file'
        |}
        |""".stripMargin
    response.setStatus(200)
    IOUtils.copy(
      new ByteArrayInputStream(info.getBytes(StandardCharsets.UTF_8)),
      response.getOutputStream
    )
  }
}
