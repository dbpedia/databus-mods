package org.dbpedia.databus_mods.lib

import io.swagger.annotations._
import javax.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._

@Controller
@RestController
@RequestMapping(Array("/a"))
abstract class AbstractDatabusModController(localRepo: String) {

  implicit val basePath: String = localRepo

  @RequestMapping(value = Array("/{publisher}/{group}/{artifact}/{version}/{fileName}"), method = Array(RequestMethod.POST))
  @ApiOperation(value = "Mod results for file")
  @ApiResponses(value = Array(
    new ApiResponse(code = 200, message = "Mod result metadata", examples = new Example(
      value = Array(new ExampleProperty(
        mediaType = "text/turtle",
        value = "<TODO> <TODO> <TODO> ."
      )))),
    new ApiResponse(code = 301, message = "Redirect to mod result metadata"),
    new ApiResponse(code = 201, message = "Process started and file is accepted"),
    new ApiResponse(code = 202, message = "Process is still running and file was accepted"),
    new ApiResponse(code = 400, message = "Process failed or file is not acceptable")
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

    DatabusModInput.apply(publisher,group,artifact,version,fileName,fileUri) match {
      case Some(databusModInput) =>
        if (databusModInput.modMetadataFile(localRepo).exists) {
          response.setStatus(200)
          response.setContentType("text/turtle")
          IOUtils.copy(
            databusModInput.modMetadataFile.newFileInputStream,
            response.getOutputStream
          )
        } else if(databusModInput.modErrorFile.exists) {
          response.setStatus(400)
        } else if(databusModInput.isRunning) {
          response.setStatus(202)
        } else {
          response.setStatus(201)
          DatabusModInputQueue.put(databusModInput)
        }
      case _ => response.setStatus(400)
    }
  }
}

