package org.dbpedia.databus_mods.lib.worker

import org.springframework.context.annotation.{ComponentScan, Configuration}

@Configuration
@ComponentScan(basePackages = Array(
  "org.dbpedia.databus_mods.lib.worker.controller",
  "org.dbpedia.databus_mods.lib.worker.service",
  "org.dbpedia.databus_mods.lib.worker.execution"
))
class AsyncWorker {

}
