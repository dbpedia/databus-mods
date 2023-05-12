package org.dbpedia.databus_mods.void

import org.dbpedia.databus.mods.core.worker.AsyncWorker
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(value = Array(classOf[AsyncWorker]))
class Worker {}

object Worker extends App {
  SpringApplication.run(classOf[Worker], args: _*)
}

