package org.dbpedia.databus_mods.lib.worker.execution

import java.io.{FileOutputStream, OutputStream}
import java.util.Calendar

import org.apache.jena.datatypes.RDFDatatype
import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.vocabulary.{RDF, RDFS}
import org.dbpedia.databus_mods.lib.util.ModelUtil.ModelWrapper
import org.dbpedia.databus_mods.lib.worker.service.FileService

class Extension(fileService: FileService, path: String, val source: String) {
  private val subjectUri = ""

  private val metadata = ModelFactory.createDefaultModel()

  private val prov = "http://www.w3.org/ns/prov#"

  metadata.setNsPrefix("prov", prov)

  metadata.addStmtToModel(subjectUri, prov + "used", s"https://databus.dbpedia.org/$path")

  def createModResult(name: String): OutputStream = {
    metadata.addStmtToModel(subjectUri, prov + "generated", name)
    new FileOutputStream(fileService.createFile(path, name))
  }

  def addProperty(property:String, value: String): Unit = {
    import org.dbpedia.databus_mods.lib.util.ModelUtil.ModelWrapper
    metadata.addStmtToModel(subjectUri,property,value)
  }

  def setType(uri: String): Unit = {
    import org.dbpedia.databus_mods.lib.util.ModelUtil.ModelWrapper
    metadata.addStmtToModel(subjectUri,RDF.`type`.getURI,uri)
  }

  def setStart(time: Calendar): Unit = {
    metadata.addStmtToModel(subjectUri, prov + "startedAtTime", new XSDDateTime(time))

  }

  def setEnd(time: Calendar): Unit = {
    metadata.addStmtToModel(subjectUri, prov + "endedAtTime", new XSDDateTime(time))
  }

  def getModel: Model = {
    metadata
  }
}