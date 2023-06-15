package org.dbpedia.databus_mods.void

import org.apache.tomcat.util.buf.UriUtil
import org.dbpedia.databus.mods.core.io.DataUtil
import org.dbpedia.databus.mods.core.io.RdfIO
import org.dbpedia.databus.mods.core.model.{ModActivity, ModActivityMetadata, ModActivityMetadataBuilder, ModActivityRequest}
import org.dbpedia.databus.mods.worker.springboot.EnableModWorkerApi
import org.dbpedia.databus.mods.worker.springboot.ModWorkerApiProfile
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, Import}

import java.net.URI

@SpringBootApplication
class RDFVoidWorker {

  private val log = LoggerFactory.getLogger(classOf[RDFVoidWorker])

  @Bean
  @EnableModWorkerApi(version = "1.0.0", profile = ModWorkerApiProfile.HttpPoll)
  def getModActivity() = new ModActivity {

    override def perform(request: ModActivityRequest, builder: ModActivityMetadataBuilder): ModActivityMetadata = {
      builder.withType("https://mods.tools.dbpedia.org/ns/rdf#VoidMod")

//      ModActivity

//      val is = DataUtil.openStream(request.accessUri))

//      val pipedRDF = RdfIO.toPipedRDF(is)

//      if (pipedRDF.hasNext) {
//        val (classPartitionMap, propertyPartitionMap) = RDFVoidUtil.calculateVoIDPartitions(pipedRDF)
//        val voidModel = RDFVoidUtil.toJenaModel(classPartitionMap, propertyPartitionMap)
//        voidModel.setNsPrefix("void", "http://rdfs.org/ns/void#")
//        voidModel.write(builder.createModResult("rdfVoid.ttl", "http://dataid.dbpedia.org/ns/mods#statisticsDerivedFrom"), "TURTLE")
//      } else {
//        log.warn(s"empty iterator")
//      }
//      is.close()

      builder.build()
    }
  }
}

object RDFVoidWorker extends App {
  SpringApplication.run(classOf[RDFVoidWorker], args: _*)
}

