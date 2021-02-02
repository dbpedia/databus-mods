package org.dbpedia.databus_mods.server.core.controller

import org.springframework.web.bind.annotation.{RequestMapping, RestController}

@RestController
@RequestMapping(value = Array("link"))
class MetadataLinkController {

  @RequestMapping
  def create(
            )
  : Unit = {

  }
}
