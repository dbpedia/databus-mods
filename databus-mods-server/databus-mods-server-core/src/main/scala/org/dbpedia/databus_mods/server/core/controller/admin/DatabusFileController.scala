package org.dbpedia.databus_mods.server.core.controller.admin

import com.fasterxml.jackson.annotation.JsonView
import org.dbpedia.databus_mods.server.core.service.DatabusFileService
import org.dbpedia.databus_mods.server.core.views.Views
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}

@RestController
@RequestMapping(value = Array("admin/databus-files"))
class DatabusFileController {

  @Autowired private var databusFileService: DatabusFileService = _

  @JsonView(value = Array(classOf[Views.DatabusFileView]))
  @RequestMapping(value = Array(""), method = Array(RequestMethod.GET, RequestMethod.DELETE))
  def databusFiles() = {
    databusFileService.getAll
  }
}
