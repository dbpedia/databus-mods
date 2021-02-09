package org.dbpedia.databus_mods.mimetype

import java.io.{BufferedInputStream, FileInputStream, InputStream}

import better.files.File
import org.apache.any23.mime.TikaMIMETypeDetector
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory}
import org.apache.jena.rdf.model.Resource
import org.apache.jena.riot.RDFDataMgr
import org.dbpedia.databus_mods.lib.worker.base.DataIDExtension
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component

@Component
class MimeTypeProcessor extends org.dbpedia.databus_mods.lib.worker.base.Process {

//  private val ianaOntology = RDFDataMgr.loadModel("http://dataid.dbpedia.org/iana/ianaOntology.ttl")


  override def run(ext: DataIDExtension): Unit = {

  }

  def checkMimeType(stream: InputStream, name: String = null): String = {
    //  Files.probeContentType(file.path)
    val detector = new TikaMIMETypeDetector()
    detector.guessMIMEType(name, stream,null).toString
  }

//  /**
//   * get MimeType URI of Iana Ontology that corresponds to calculated mimeType
//   *
//   * @param mimeType calculated mimeType
//   * @return Resource of ianaOntology that matches with the mimeType
//   */
//  def getMimeTypeFromIanaOntology(mimeType: String): Resource = {
//    val queryStr =
//      s"""
//         |SELECT ?s
//         |WHERE {
//         | ?s ?p ?o .
//         | FILTER (regex(str(?s), '$mimeType','i'))
//         |}
//         |LIMIT 1
//      """.stripMargin
//
//    val query = QueryFactory.create(queryStr)
//    val qe = QueryExecutionFactory.create(query, ianaOntology)
//
//    val results = qe.execSelect()
//    val result = results.next().getResource("s")
//    qe.close()
//
//    result
//  }
}

object MimeTypeProcessor extends App {

  val p = new MimeTypeProcessor

  val resolver = new PathMatchingResourcePatternResolver()
  val testfiles = resolver.getResources("testfiles/*")


  import scala.collection.JavaConverters._
  import scala.collection.JavaConversions._
  testfiles.foreach({
    resource =>
      println(resource)
      println(p.checkMimeType(new BufferedInputStream(resource.getInputStream),resource.getFile.getName))
  })


//  p.checkMimeType()
}