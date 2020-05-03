package org.dbpedia.databus.util

import java.net.URL

import org.apache.jena.riot.Lang

import scala.collection.mutable

object MimeTypeGetter {

  def getRDFFormat(url:URL): Lang= {

    val querystr =
      s"""
         |PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
         |PREFIX dcat: <http://www.w3.org/ns/dcat#>
         |
        |SELECT DISTINCT ?type
         |WHERE {
         |  ?distribution dcat:mediaType ?type .
         |  ?distribution dcat:downloadURL <${url}> .
         |}
      """.stripMargin


    val mimeTypeRDFMap:mutable.HashMap[String,Lang] = mutable.HashMap.empty
    mimeTypeRDFMap.put("http://dataid.dbpedia.org/ns/mt#ApplicationNTriples",Lang.NT)
    mimeTypeRDFMap.put("http://dataid.dbpedia.org/ns/mt#TextTurtle", Lang.TTL)
    mimeTypeRDFMap.put("http://dataid.dbpedia.org/ns/mt#ApplicationRDFXML", Lang.RDFXML)
    mimeTypeRDFMap.put("http://dataid.dbpedia.org/ns/mt#ApplicationJson", Lang.JSONLD)

    val result = org.dbpedia.databus.client.sparql.QueryHandler
      .executeQuery(querystr)
      .head
      .getResource("?type")
      .toString

    mimeTypeRDFMap.getOrElse(result, Lang.TTL)
  }


}
