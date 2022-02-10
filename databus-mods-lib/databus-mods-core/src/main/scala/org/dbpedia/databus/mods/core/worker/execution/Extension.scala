package org.dbpedia.databus.mods.core.worker.execution

import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.vocabulary.{RDF, XSD}
import org.dbpedia.databus.mods.core.util.ModelUtil.ModelWrapper
import org.dbpedia.databus.mods.core.worker.service.FileService

import java.io.{FileOutputStream, OutputStream}
import java.util.Calendar

class Extension(fileService: FileService, path: String, val databusID: String, val source: String) {
  private val subjectUri = "metadata.ttl"

  private val metadata = ModelFactory.createDefaultModel()

  private val prov = "http://www.w3.org/ns/prov#"

  metadata.setNsPrefix("prov", prov)
  metadata.setNsPrefix("xsd", XSD.NS)
  metadata.setNsPrefix("mod", "http://dataid.dbpedia.org/ns/mod#")
  metadata.addStmtToModel(subjectUri, prov + "used", databusID)

  def createModResult(name: String, backLinkWith: String = null): OutputStream = {
    if (null != backLinkWith)
      metadata.addStmtToModel(name, backLinkWith, databusID)
    metadata.addStmtToModel(subjectUri, prov + "generated", name)
    new FileOutputStream(fileService.createFile(path, name))
  }

  def addProperty(property: String, value: String): Unit = {
    metadata.addStmtToModel(subjectUri, property, value)
  }

  def addPrefix(label: String, ns: String): Unit = {
    metadata.setNsPrefix(label, ns)
  }

  def setType(uri: String): Unit = {
    metadata.addStmtToModel(subjectUri, RDF.`type`.getURI, uri)
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