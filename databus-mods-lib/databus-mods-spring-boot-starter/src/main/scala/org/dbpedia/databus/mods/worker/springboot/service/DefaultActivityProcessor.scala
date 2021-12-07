package org.dbpedia.databus.mods.worker.springboot.service

import org.apache.jena.rdf.model.{ModelFactory, ResourceFactory}
import org.apache.jena.vocabulary.RDFS
import org.dbpedia.databus.dataid.SingleFile
import org.dbpedia.databus.mods.model.{ModActivityMetadata, Vocab}
import org.graalvm.compiler.lir.CompositeValue.Component

@Component
class DefaultActivityProcessor extends ModActivity {

  override def process(modActivityMetadata: ModActivityMetadata): Unit = {
    val dbusSF = modActivityMetadata.dbusSF

    val model = ModelFactory.createDefaultModel()
    model.add(
      ResourceFactory.createResource(dbusSF.uri),
      RDFS.seeAlso,
      ResourceFactory.createResource(dbusSF.downloadURL)
    )
    modActivityMetadata.addMetadata(model)
  }
}
