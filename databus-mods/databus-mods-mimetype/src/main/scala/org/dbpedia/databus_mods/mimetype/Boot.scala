package org.dbpedia.databus_mods.mimetype

import org.dbpedia.databus.mods.core.worker.AsyncWorker
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(value = Array(classOf[AsyncWorker]))
class Boot {}

object Boot extends App {
  SpringApplication.run(classOf[Boot], args: _*)
}
