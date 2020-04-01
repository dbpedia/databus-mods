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


/**
 *
 */
object Index {

  private val endpoint: String = "https://databus.dbpedia.org/repo/sparql"

  def main(args: Array[String]): Unit = {
    println(DerbyHandler.databaseURL)
    updateIndex("dbpedia/mappings/")
    DerbyHandler.printNewResultSets
    DerbyHandler.setStatusProcessed("shatest")

  }

  /**
   * takes a pattern in the form of "dbpedia/mappings/%" as used in LIKE
   *
   * @param pattern defines which indexes are updated
   */
  def updateIndex(pattern: String) {


    val count:Int = countResults(pattern)
    val runs:Int = count/10000
//    println(s"Number of Results: $count")
    var offset = 0

    for(i <- 0 to runs){

      val sparql =
        s"""
           |PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
           |PREFIX dct:    <http://purl.org/dc/terms/>
           |PREFIX dcat:   <http://www.w3.org/ns/dcat#>
           |PREFIX db:     <https://databus.dbpedia.org/>
           |PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
           |PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
           |
         |SELECT ?dataset ?version ?distribution ?downloadURL ?shaSum WHERE
           |{
           |  ?dataset dataid:version ?version .
           |  FILTER regex(?version, <https://databus.dbpedia.org/$pattern.*>).
           |  ?dataset dcat:distribution ?distribution .
           |  ?distribution  dcat:downloadURL ?downloadURL .
           |  ?distribution  dataid:sha256sum ?shaSum .
           |}
           |LIMIT 10000
           |OFFSET $offset
           |""".stripMargin


      val query: Query = QueryFactory.create(sparql)
      val queryExec = QueryExecutionFactory.sparqlService(endpoint, query)
      val resultSet = queryExec.execSelect()

      while (resultSet.hasNext) {
        val querySolution = resultSet.next()

        DerbyHandler.addIfNotExists(
          querySolution.getLiteral("shaSum").getString,
          querySolution.getResource("downloadURL").getURI,
          querySolution.getResource("dataset").getURI,
          querySolution.getResource("version").getURI,
          querySolution.getResource("distribution").getURI)
      }

      queryExec.close()

      offset += 10000
    }

    }

  def countResults(pattern:String): Int ={

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

}
