package org.dbpedia.databus_mods.filemetrics

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.orc.DataMask.Standard
import org.dbpedia.databus_mods.lib.worker.execution.{Extension, ModProcessor}
import org.springframework.boot.{CommandLineRunner, SpringApplication}
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.stereotype.Component
import sun.tools.jar.CommandLine

@SpringBootApplication
@Import(value = Array(classOf[org.dbpedia.databus_mods.lib.worker.AsyncWorker]))
class Worker() {}

object Worker extends App {
  SpringApplication.run(classOf[Worker], args: _*)
}
