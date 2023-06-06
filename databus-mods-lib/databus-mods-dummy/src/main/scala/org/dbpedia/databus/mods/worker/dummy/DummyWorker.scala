package org.dbpedia.databus.mods.worker.dummy

import org.dbpedia.databus.mods.core.model.{ModActivity, ModActivityMetadata, ModActivityMetadataBuilder, ModActivityRequest}
import org.dbpedia.databus.mods.worker.springboot.{EnableModWorkerApi, ModWorkerApiProfile}
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

import scala.util.Random;

@SpringBootApplication
class DummyWorker {

  @Bean
  @EnableModWorkerApi(version = "1.0.0", profile = ModWorkerApiProfile.HttpPoll)
  def getModActivity: ModActivity = new ModActivity {
    override def perform(request: ModActivityRequest, builder: ModActivityMetadataBuilder): ModActivityMetadata = {
      Thread.sleep(1000)
      builder.withStatSummary((Random.nextInt(100)/100.0).toString).build()
    }
  }
}


object DummyWorker extends App {
  SpringApplication.run(classOf[DummyWorker], args: _*)
}