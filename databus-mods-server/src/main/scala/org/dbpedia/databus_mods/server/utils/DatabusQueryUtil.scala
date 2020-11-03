package org.dbpedia.databus_mods.server.utils

import org.apache.jena.query.{QueryExecutionFactory, QueryFactory}
import org.dbpedia.databus_mods.server.database.DatabusFile

import scala.collection.mutable.ArrayBuffer

object DatabusQueryUtil {

  def getUpdates(query: String): Array[DatabusFile] = {
    val limit = 10000
    var responseSize, offset = 0
    val databusFilesBuffer = new ArrayBuffer[DatabusFile]()

    do {
      responseSize = 0
      val sparql = QueryFactory.create(query + s" LIMIT $limit OFFSET $offset")
      val queryExec = QueryExecutionFactory.sparqlService(
        "https://databus.dbpedia.org/repo/sparql", sparql
      )
      val resultSet = queryExec.execSelect()

      while (resultSet.hasNext) {
        responseSize += 1
        val qs = resultSet.next()
        databusFilesBuffer.append(
          DatabusFile(
            qs.getResource("file").getURI,
            qs.getLiteral("sha256sum").getLexicalForm,
            qs.getResource("downloadURL").getURI
          )
        )
      }
      queryExec.close()
      offset += limit
    } while (responseSize != 0)
    databusFilesBuffer.toArray
  }
}
