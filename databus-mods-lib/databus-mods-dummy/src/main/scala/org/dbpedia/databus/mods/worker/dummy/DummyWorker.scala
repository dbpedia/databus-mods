package org.dbpedia.databus.mods.worker.dummy

import ch.qos.logback.classic.{Level, Logger}
import org.dbpedia.databus.mods.core.model.{ModActivity, ModActivityMetadata, ModActivityMetadataBuilder, ModActivityRequest}
import org.dbpedia.databus.mods.worker.springboot.{EnableModWorkerApi, ModWorkerApiProfile}
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

import scala.util.Random;

@SpringBootApplication
@EnableModWorkerApi(version = "1.0", profile = ModWorkerApiProfile.HttpPoll)
class DummyWorker {

  @Bean
  def dummyActivity = new ModActivity {
    override def perform(request: ModActivityRequest, builder: ModActivityMetadataBuilder): ModActivityMetadata = {
      Thread.sleep(1000)
      builder.withStatSummary((Random.nextInt(100) / 100.0).toString).build()
    }
  }
}

object DummyWorker extends App {
  val log = LoggerFactory.getLogger("org.spring").asInstanceOf[Logger]
  log.setLevel(Level.DEBUG)
  SpringApplication.run(classOf[DummyWorker], args: _*)
}

