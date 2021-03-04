package org.dbpedia.databus_mods.server.core.execution

import java.net.URI

import org.apache.jena.rdf.model.{Model, ModelFactory, Property, ResourceFactory, Selector, SimpleSelector}
import org.dbpedia.databus_mods.server.core.persistence.Task

import scala.collection.JavaConversions._

// TODO is modMetadata released/de referenced after construction?
class MetadataExtension(val task: Task,val rdfByteArray: Array[Byte],val rdfBaseURI: URI) {

  private final val provGenerated: Property = ResourceFactory.createProperty("http://www.w3.org/ns/prov#generated")


//  val modResultURIs: List[URI] = {
//
//    modMetadata.query(new SimpleSelector(null,provGenerated,null,null))
//      .listStatements()
//      .toList.map({
//      stmt => new URI(stmt.getObject.asResource().getURI)
//    }).toList
//  }
}
