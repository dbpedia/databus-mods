package org.dbpedia.databus_mods.spo

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.dbpedia.databus_mods.lib.worker.base.{FileRepository, DataIDExtension, Process, WorkerTask}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Component

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = Array("org.dbpedia.databus_mods.lib.worker.base"))
class Worker {

  @Component
  class VoidProcess extends Process {

    @Autowired var repository: FileRepository = _

    def run(dataIDExtension: DataIDExtension): Unit = {
      IOUtils.write("test",dataIDExtension.createModResult("rdfVoid.ttl"), StandardCharsets.UTF_8)
    }
  }

}

object Worker extends App {
  SpringApplication.run(classOf[Worker],args: _ *)
}

