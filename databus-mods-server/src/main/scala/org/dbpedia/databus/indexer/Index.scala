/*-
 * #%L
 * Indexing the Databus
 * %%
 * Copyright (C) 2018 - 2020 Sebastian Hellmann (on behalf of the DBpedia Association)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.dbpedia.databus.indexer

import org.apache.jena.query.{Query, QueryExecutionFactory, QueryFactory}

import scala.collection.mutable.ListBuffer


/**
  * Example
  */
object Index {
  def main(args: Array[String]): Unit = {
    val old = "jdbc:derby:.indexdb;create=true"

    /** val index = new Index(".indexdb", List = ("").)
      *index.updateIndex("dbpedia/")
      * //index.printNewResultSets
      *index.derbyHandler.setStatusProcessed("shatest")
      *index.derbyHandler.shutdown
      */
  }
}

/**
  * Retrieves and stores information of files on the Databus in a local database file
  *
  * Note: this allows to have many indexes, separated in different dbs
  * If only one index is needed, then use the same database
  *
  * @param indexdbfile the local path, where the DB is created/reused
  */
class Index(val indexdbfile: String, val patterns: java.util.List[String]) {


  private val endpoint: String = "https://databus.dbpedia.org/repo/sparql"
  private val derbyHandler: DerbyDbHandler = DerbyDbFactory.init(indexdbfile)


  def updateIndexes(): Unit = {
    var i = 0
    while (i < patterns.size()) {
      updateIndex(patterns.get(i))
      i += 1
    }
  }

  /**
    * TODO filter (version > last updated)
    *
    * loads ALL records matching the pattern into the local database
    * FILTER regex(?version, <https://databus.dbpedia.org/$pattern.*>
    *
    * Pattern examples for user, group, artifact, version
    * dbpedia/
    * dbpedia/mappings/
    * dbpedia/mappings/infobox-properties/
    * dbpedia/mappings/infobox-properties/2020.03.01
    *
    * @param pattern a pattern in the form of "dbpedia/mappings/" .
    */
  def updateIndex(pattern: String) {

    println("pattern: " + pattern)

    val count: Int = countResults(pattern)
    val runs: Int = math.ceil(count.toDouble / 10000.0).toInt
    println(s"Number of Results / Runs: $count / $runs")
    var offset = 0

    for (i <- 1 to runs) {
      println(s"Run: $i")
      val sparql =
        s"""
           |PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
           |PREFIX dct:    <http://purl.org/dc/terms/>
           |PREFIX dcat:   <http://www.w3.org/ns/dcat#>
           |PREFIX db:     <https://databus.dbpedia.org/>
           |PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
           |PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
           |
           |SELECT ?dataset ?version ?distribution ?downloadURL ?file ?shaSum WHERE
           |{
           |  ?dataset dataid:version ?version .
           |  FILTER regex(?version, <https://databus.dbpedia.org/$pattern.*>).
           |  ?dataset dcat:distribution ?distribution .
           |  ?distribution  dcat:downloadURL ?downloadURL .
           |  ?distribution  dataid:sha256sum ?shaSum .
           |  ?distribution  dataid:file ?file .
           |}
           |LIMIT 10000
           |OFFSET $offset
           |""".stripMargin


      val query: Query = QueryFactory.create(sparql)
      val queryExec = QueryExecutionFactory.sparqlService(endpoint, query)
      val resultSet = queryExec.execSelect()

      while (resultSet.hasNext) {
        val querySolution = resultSet.next()

        derbyHandler.addIfNotExists(
          querySolution.getLiteral("shaSum").getString,
          querySolution.getResource("downloadURL").getURI,
          querySolution.getResource("dataset").getURI,
          querySolution.getResource("version").getURI,
          querySolution.getResource("file").getURI,
          querySolution.getResource("distribution").getURI)
      }

      queryExec.close()

      offset += 10000
    }

  }

  /**
    * auxiliary function
    * counts results of the pattern matched on the databus
    *
    * @param pattern a pattern in the form of "dbpedia/mappings/" .
    * @return
    */
  def countResults(pattern: String): Int = {

    val sparql =
      s"""
         |PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
         |PREFIX dcat:   <http://www.w3.org/ns/dcat#>
         |
         |SELECT (COUNT(?shaSum) AS ?count) WHERE
         |{
         |  ?dataset dataid:version ?version .
         |  FILTER regex(?version, <https://databus.dbpedia.org/$pattern.*>).
         |  ?dataset dcat:distribution ?distribution .
         |  ?distribution  dataid:sha256sum ?shaSum .
         |}
      """.stripMargin

    val query: Query = QueryFactory.create(sparql)
    val queryExec = QueryExecutionFactory.sparqlService(endpoint, query)
    val resultSet = queryExec.execSelect()

    val count = {
      if (resultSet.hasNext) resultSet.next().getLiteral("count").getInt
      else 0
    }

    queryExec.close()

    count
  }

  /**
    * marks the file with the particular shasum as "processed"
    *
    * @param shasum the shasum of the file
    */
  def setStatusProcessed(shasum: String, processorUID: String): Unit = derbyHandler.setStatusProcessed(shasum, processorUID)

  def getStatuses(shasum: String): ListBuffer[String] = derbyHandler.getStatus(shasum)


  /**
    * for debugging
    */
  def printNewResultSets: Unit = {
    val rs = getNewResultSet
    while (rs.next) {
      val item = rs.getItem
      System.out.println(item)
    }
    rs.close
  }

  /**
    * retrieves all with status "open"
    *
    * @return ResultSet for iterating into threads
    */
  def getNewResultSet: ItemSet = derbyHandler.getNewResultSet

}
