package org.dbpedia.databus_mods.server.core.utils

import java.sql.Timestamp

import org.apache.jena.query.{QueryExecutionFactory, QueryFactory, Syntax}
import org.dbpedia.databus_mods.server.core.persistence.DatabusFile

import scala.collection.mutable.ArrayBuffer

object DatabusQueryUtil {

  def getUpdates(query: String): Array[DatabusFile] = {
    val limit = 10000
    var responseSize, offset = 0
    val databusFilesBuffer = new ArrayBuffer[DatabusFile]()

    do {
      responseSize = 0
      val sparql = QueryFactory.create(query + s" LIMIT $limit OFFSET $offset", Syntax.syntaxSPARQL_11)
      val queryExec = QueryExecutionFactory.sparqlService(
        "https://databus.dbpedia.org/repo/sparql", sparql
      )
      val resultSet = queryExec.execSelect()

      while (resultSet.hasNext) {
        responseSize += 1
        val qs = resultSet.next()
        databusFilesBuffer.append(
          new DatabusFile(
            qs.getResource("file").getURI,
            qs.getResource("downloadURL").getURI,
            qs.getLiteral("sha256sum").getLexicalForm,
            DateUtil.parseToTimestamp(qs.get("issued").asLiteral().getLexicalForm)
          )
        )
      }
      queryExec.close()
      offset += limit
    } while (responseSize != 0)
    databusFilesBuffer.toArray
  }

  def queryDatabusFileByURI(uri: String): Option[DatabusFile] = {
    val query =
      s"""PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
         |PREFIX dct:    <http://purl.org/dc/terms/>
         |PREFIX dcat:   <http://www.w3.org/ns/dcat#>
         |SELECT DISTINCT ?sha256sum ?issued ?downloadURL WHERE {
         |  ?dataid a dataid:Dataset .
         |  ?dataid dcat:distribution ?distribution .
         |  ?distribution dataid:file <${uri}> .
         |  ?distribution dataid:sha256sum ?sha256sum .
         |  ?distribution dct:issued ?issued .
         |  ?distribution dcat:downloadURL ?downloadURL .
         |}""".stripMargin
    val sparql = QueryFactory.create(query)
    val queryExec = QueryExecutionFactory.sparqlService("https://databus.dbpedia.org/repo/sparql", sparql)
    val resultSet = queryExec.execSelect()
    val possibleDatabusFile = if (resultSet.hasNext) {
      val qs = resultSet.next()
      Some(new DatabusFile(
        uri,
        qs.getResource("downloadURL").getURI,
        qs.getLiteral("sha256sum").getLexicalForm,
        DateUtil.parseToTimestamp(qs.get("issued").asLiteral().getLexicalForm)
      ))
    } else {
      None
    }
    queryExec.close()
    possibleDatabusFile
  }

  def main(args: Array[String]): Unit = {
    new Timestamp(System.currentTimeMillis())
  }
}
