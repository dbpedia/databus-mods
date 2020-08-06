package org.dbpedia.databus_mods.void

import org.dbpedia.databus_mods.lib.AbstractDatabusModController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.stereotype.Controller

@Controller
@EnableAutoConfiguration
class DatabusModController @Autowired()(config: Config) extends AbstractDatabusModController(config.volumes.localRepo)
