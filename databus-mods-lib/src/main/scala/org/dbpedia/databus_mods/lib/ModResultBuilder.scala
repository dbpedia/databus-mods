package org.dbpedia.databus_mods.lib

import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.{Model, ResourceFactory}

object ModResultBuilder {

  def addModInformationToModel(model: Model, baseUri: String, databusModInput: DatabusModInput, modName: String): Unit = {

    import scala.collection.JavaConverters.mapAsJavaMapConverter

    val prefixMap: Map[String, String] = Map(
      "mod" -> "http://dataid.dbpedia.org/ns/mod.ttl#",
      "prov" -> "http://www.w3.org/ns/prov#",
      "dataid-mt" -> "http://dataid.dbpedia.org/ns/mt#",
      "dcat" -> "http://www.w3.org/ns/dcat#"
    )

    model.setNsPrefixes(prefixMap.asJava)

    val provFileResource = ResourceFactory.createResource(s"https://databus.dbpedia.org/${databusModInput.id}")
    val modResource = ResourceFactory.createResource(s"${databusModInput.modMetadataFile(baseUri)}#this")

    model.add(
      ResourceFactory.createStatement(
        modResource,
        ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myModVoc")}$modName")))

    model.add(
      ResourceFactory.createStatement(
        modResource,
        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("prov")}used"),
        provFileResource))

    model.add(
      ResourceFactory.createStatement(
        modResource,
        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("prov")}endedAtTime"),
        ResourceFactory.createTypedLiteral(java.time.ZonedDateTime.now.toString, XSDDatatype.XSDdateTime)))

    def addStmt(): Unit = {

    }

    addStmt()
    addStmt()
    addStmt()
    addStmt()

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.svg"),
        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("mod")}svgDerivedFrom"),
        provFileResource))

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.html"),
        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("mod")}htmlDerivedFrom"),
        provFileResource))

//    val stmt = ResourceFactory.createStatement(
//      modResource,
//      ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("prov")}generated"),
////      ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.html"))
//      ResourceFactory.createResource(databusModInput.modResourceFile("mod.html"))

//    model.add(stmt)
//
//    model.add(
//      stmt.changeObject(ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.svg")))
//
//    model.add(
//      stmt.changeObject(ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.ttl#result")))
//  }

  }
}
