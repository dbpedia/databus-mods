package org.dbpedia.databus_mods.mimetype

import java.io.{BufferedInputStream, InputStream}
import java.net.URI
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory}
import org.apache.jena.rdf.model.Resource
import org.apache.jena.riot.RDFDataMgr
import org.apache.tika.config.TikaConfig
import org.dbpedia.databus.mods.core.util.UriUtil
import org.dbpedia.databus.mods.core.worker.execution.{Extension, ModProcessor}
import org.springframework.stereotype.Component

@Component
class MimeTypeProcessor extends ModProcessor {

  private val ianaOntology = RDFDataMgr.loadModel("http://dataid.dbpedia.org/iana/ianaOntology.ttl")

  def process(ext: Extension): Unit = {
    //    modelHelper.addStmtToModel(resultURI, "http://www.w3.org/ns/dcat#mediaType", getMimeTypeFromIanaOntology(mimeType))
    ///home/marvin/src/github.com/dbpedia/databus-mods/databus-mods/databus-mods-mimetype/src/main/scala/org/dbpedia/databus_mods/mimetype
    //    if (compression.nonEmpty) {
    //      modelHelper.addStmtToModel(resultURI, "http://www.w3.org/ns/dcat#compression", s"http://dataid.dbpedia.org/ns/mt#$compression")

    val inputStream = new BufferedInputStream(UriUtil.openStream(new URI(ext.source)))
    val mimeType = checkMimeType(inputStream)
    inputStream.close()

    ext.setType("https://mods.tools.dbpedia.org/ns/file#MimeTypeMod")
    ext.addPrefix("","https://mods.tools.dbpedia.org/ns/file#")
    val mimeTypeResource = getMimeTypeFromIanaOntology(mimeType)
    ext.addProperty("https://mods.tools.dbpedia.org/file#mimeType",mimeTypeResource.getURI)
  }

  def checkMimeType(stream: InputStream, name: String = null): String = {
    //  Files.probeContentType(file.path)
    val detector = TikaConfig.getDefaultConfig.getDetector
    detector.detect(stream,null).toString
  }

    /**
      * get MimeType URI of Iana Ontology that corresponds to calculated mimeType
      *
      * @param mimeType calculated mimeType
      * @return Resource of ianaOntology that matches with the mimeType
      */
    private def getMimeTypeFromIanaOntology(mimeType: String): Resource = {
      val queryStr =
        s"""
           |SELECT ?s
           |WHERE {
           | ?s ?p ?o .
           | FILTER (regex(str(?s), '$mimeType','i'))
           |}
           |LIMIT 1
        """.stripMargin

      val query = QueryFactory.create(queryStr)
      val qe = QueryExecutionFactory.create(query, ianaOntology)

      val results = qe.execSelect()
      val result = results.next().getResource("s")
      qe.close()

      result
    }
}