package org.dbpedia.databus_mods.lib.util

import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}
import org.dbpedia.databus_mods.lib.DatabusModInput

class ModModel(databusModInput: DatabusModInput, baseUri: String) {

  val subject: String = databusModInput.modMetadataFile.pathAsString

  //  private vocabModel =

  private implicit val baseDir: String = baseUri
  private val model = ModelFactory.createDefaultModel()

  object Prefixes {
    val prov = "http://www.w3.org/ns/prov#"
  }

  private val uriByPrefix: Map[String, String] = Map(
    "mod" -> "http://dataid.dbpedia.org/ns/mod.ttl#",
    "prov" -> Prefixes.prov,
    "dataid-mt" -> "http://dataid.dbpedia.org/ns/mt#",
    "dcat" -> "http://www.w3.org/ns/dcat#"
  )


  //  databusModInput.modResourceFile("") as fileName
  def generatesProvDerived(fileName: String, property: String): Unit = {

    //    val stmt = ResourceFactory.createStatement(
    //      ResourceFactory.createResource(subject),
    //      ResourceFactory.createProperty(property),
    //      provFileResource
    //    )
    //    )
  }
}
