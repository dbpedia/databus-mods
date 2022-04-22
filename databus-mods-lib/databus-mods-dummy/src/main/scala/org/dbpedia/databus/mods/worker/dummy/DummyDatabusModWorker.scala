package org.dbpedia.databus.mods.worker.dummy

import org.apache.jena.datatypes.xsd.XSDDateTime
import org.dbpedia.databus.mods.model.{ModActivity, ModActivityMetadata, ModActivityMetadataBuilder, ModActivityRequest, ModResultFactory}
import org.dbpedia.databus.mods.worker.springboot.EnableModWorkerApi
import org.dbpedia.databus.mods.worker.springboot.controller.WorkerApiProfile
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

import java.util.Calendar
import scala.util.Random;

@SpringBootApplication
class DummyDatabusModWorker {

  @Bean
  @EnableModWorkerApi(version = "1.0.0", profile = WorkerApiProfile.Polling)
  def getModActivity: ModActivity = new ModActivity {
    override def perform(request: ModActivityRequest, builder: ModActivityMetadataBuilder): ModActivityMetadata = {

      builder.addGenerated(ModResultFactory.enrichment(request.id,"result.ttl"))
      builder.addGenerated(ModResultFactory.enrichment(request.id,"result2.ttl"))

      builder.withStatSummary((Random.nextInt(100)/100.0).toString).build()
    }
  }
}


object DummyDatabusModWorker extends App {
  SpringApplication.run(classOf[DummyDatabusModWorker], args: _*)
}