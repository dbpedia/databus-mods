package org.dbpedia.databus_mods.server.controller

import org.dbpedia.databus_mods.lib.AbstractDatabusModController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RestController}

@RestController
@RequestMapping(Array("/demo"))
class DemoMod @Autowired()(config: DemoModConfig) extends AbstractDatabusModController(config.localRepo)
