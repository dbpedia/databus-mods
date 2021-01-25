package org.dbpedia.databus_mods.lib.worker.base

import java.io.{FileOutputStream, OutputStream}
import java.net.URI
import java.util.Calendar

import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.dbpedia.databus_mods.lib.util.ModelUtil.ModelWrapper

class DataIDExtension(repository: FileRepository, path: String,val source: URI) {

  private val subjectUri = ""

  private val metadata = ModelFactory.createDefaultModel()

  private val prov = "http://www.w3.org/ns/prov#"

  metadata.setNsPrefix("prov",prov)

  metadata.addStmtToModel(subjectUri, prov+"used", s"https://databus.dbpedia.org/$path")

  def createModResult(name: String): OutputStream = {
    metadata.addStmtToModel(subjectUri,prov+"generated",name)
    new FileOutputStream(repository.createFile(path, name))
  }

  def setStart(time: Calendar): Unit = {
    metadata.addStmtToModel(subjectUri,prov+"startedAtTime",new XSDDateTime(time))

  }
  def setEnd(time: Calendar): Unit = {
    metadata.addStmtToModel(subjectUri,prov+"endedAtTime",new XSDDateTime(time))
  }

  def getModel: Model = {
    metadata
  }
}
