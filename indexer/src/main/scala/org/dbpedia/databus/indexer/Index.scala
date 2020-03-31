package org.dbpedia.databus.indexer

/**
 *
 */
object  Index {


  def main(args: Array[String]): Unit = {
    updateIndex("dbpedia/mappings/%")
  }

  /**
   * takes a pattern in the form of "dbpedia/mappings/%" as used in LIKE
   * @param pattern
   */
  def updateIndex(pattern:String){


    val sparql = s"""
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
       |  FILTER (?version LIKE <https://databus.dbpedia.org/${pattern}> ).
       |  ?dataset dcat:distribution ?distribution .
       |  ?distribution  dcat:downloadURL ?downloadURL .
       |  ?distribution  dataid:sha256sum ?shaSum .
       |}
       |""".stripMargin

        //TODO SPARQL all with limit and offset



  }

  def newFiles () {}

}
