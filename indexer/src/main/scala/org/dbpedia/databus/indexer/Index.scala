package org.dbpedia.databus.indexer

/**
 *
 */
object  Index {


  def main(args: Array[String]): Unit = {
    updateIndex("")
  }

  /**
   * takes a pattern in the form of $user// *
   * @param pattern
   */
  def updateIndex(pattern:String){


    val sparql = """PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
                   |PREFIX dct:    <http://purl.org/dc/terms/>
                   |PREFIX dcat:   <http://www.w3.org/ns/dcat#>
                   |PREFIX db:     <https://databus.dbpedia.org/>
                   |PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                   |PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
                   |
                   |SELECT * WHERE
                   |{   	?dataset dataid:version ?version .
                   |        FILTER (?version LIKE <https://databus.dbpedia.org/dbpedia/%> ).
                   |}
                   |""".stripMargin

      //TODO sparql and save
      val p ="dbpedia/generic/*/*"
      val s: Array[String] = p.split('/')
      val account = s.apply(0)
      val group = s.apply(1)
      val artifact = s.apply(2)
      val version = s.apply(3)

      if (version!= "*"



  }

  def newFiles () {}

}
