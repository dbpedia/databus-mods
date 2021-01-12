package org.dbpedia.databus_mods.void.gtd

import java.util.Calendar

import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.rdf.model.{Model, ModelFactory}


class DatabusMod(
                  dataIdFilePath: String,
                  generated: List[String],
                  startTime: Calendar,
                  endTime: Calendar,
                  uri: String = "mod.ttl") {

  private val model: Model = ModelFactory.createDefaultModel()

  import ModelUtil.ModelWrapper

  private val prov = "http://www.w3.org/ns/prov#"

  // http://www.w3.org/ns/prov#

  model.addStmtToModel(uri, prov+"used", s"https://databus.dbpedia.org/$dataIdFilePath")
  generated.foreach(g => model.addStmtToModel(uri,prov+"generated",g))
  model.addStmtToModel(uri,prov+"startedAtTime",new XSDDateTime(startTime))
  model.addStmtToModel(uri,prov+"endedAtTime",new XSDDateTime(endTime))

  def getModel: Model = model
}
